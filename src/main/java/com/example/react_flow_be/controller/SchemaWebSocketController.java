package com.example.react_flow_be.controller;

import com.example.react_flow_be.dto.websocket.*;
import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
            NodePositionUpdateMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Updating node position: {} to ({}, {})", 
                    message.getModelName(), message.getPositionX(), message.getPositionY());
            
            // Cập nhật position trong database
            boolean updated = schemaVisualizerService.updateModelPosition(
                    message.getModelName(),
                    message.getPositionX(),
                    message.getPositionY()
            );
            
            if (updated) {
                // Broadcast đến tất cả client khác (trừ người gửi)
                WebSocketResponse<NodePositionUpdateMessage> response = 
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
    
    @MessageMapping("/updateField")
    public void updateField(
            FieldUpdateMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Updating field: {} - {} ({})", 
                    message.getFieldName(), message.getFieldType(), message.getModelName());
            
            // Cập nhật field trong database
            boolean updated = schemaVisualizerService.updateField(
                    message.getFieldId(),
                    message.getFieldName(),
                    message.getFieldType()
            );
            
            if (updated) {
                // Broadcast đến tất cả client
                WebSocketResponse<FieldUpdateMessage> response = 
                    WebSocketResponse.success("FIELD_UPDATE", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to update field", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error updating field", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }
}