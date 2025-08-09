package com.example.react_flow_be.controller;

import com.example.react_flow_be.dto.websocket.*;
import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SchemaWebSocketController {
    
    private final SchemaVisualizerService schemaVisualizerService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/updateNodePosition")
    public void updateNodePosition(
            ModelUpdateMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Updating node position: {} to ({}, {})", 
                    message.getNodeId(), message.getPositionX(), message.getPositionY());
            
            // Cập nhật position trong database - sử dụng nodeId thay vì modelName
            boolean updated = schemaVisualizerService.updateModelPosition(
                    message.getNodeId(), // Changed from getModelName() to getNodeId()
                    message.getPositionX(),
                    message.getPositionY()
            );
            
            if (updated) {
                // Broadcast đến tất cả client khác (trừ người gửi)
                WebSocketResponse<ModelUpdateMessage> response = 
                    WebSocketResponse.success("NODE_POSITION_UPDATE", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                // Gửi error về cho client gửi
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to update node position", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error updating node position", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }
    
    @MessageMapping("/updateAttribute")
    public void updateAttribute(
            AttributeUpdateMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Updating Attribute: {} - {} ({})", 
                    message.getAttributeName(), message.getAttributeType(), message.getModelName());
            
            // Cập nhật Attribute trong database
            boolean updated = schemaVisualizerService.updateAttribute(
                    message.getAttributeId(),
                    message.getAttributeName(),
                    message.getAttributeType()
            );
            
            if (updated) {
                // Broadcast đến tất cả client
                WebSocketResponse<AttributeUpdateMessage> response = 
                    WebSocketResponse.success("Attribute_UPDATE", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to update Attribute", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error updating Attribute", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }
}