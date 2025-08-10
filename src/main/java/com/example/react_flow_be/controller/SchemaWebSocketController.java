// src/main/java/com/example/react_flow_be/controller/SchemaWebSocketController.java
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
            
            boolean updated = schemaVisualizerService.updateModelPosition(
                    message.getModelId(), 
                    message.getPositionX(),
                    message.getPositionY()
            );
            
            if (updated) {
                WebSocketResponse<ModelUpdateMessage> response = 
                    WebSocketResponse.success("NODE_POSITION_UPDATE", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
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
            
            boolean updated = schemaVisualizerService.updateAttribute(
                    message.getAttributeId(),
                    message.getAttributeName(),
                    message.getAttributeType()
            );
            
            if (updated) {
                WebSocketResponse<AttributeUpdateMessage> response = 
                    WebSocketResponse.success("FIELD_UPDATE", message, sessionId);
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

    @MessageMapping("/togglePrimaryKey")
    public void togglePrimaryKey(
            TogglePrimaryKeyMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Toggling primary key for attribute ID: {} in model: {}", 
                    message.getAttributeId(), message.getModelName());
            
            boolean updated = schemaVisualizerService.togglePrimaryKey(
                    message.getAttributeId()
            );
            
            if (updated) {
                WebSocketResponse<TogglePrimaryKeyMessage> response = 
                    WebSocketResponse.success("TOGGLE_PRIMARY_KEY", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to toggle primary key", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error toggling primary key", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }

    @MessageMapping("/toggleForeignKey")
    public void toggleForeignKey(
            ToggleForeignKeyMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Toggling foreign key for attribute ID: {} in model: {}", 
                    message.getAttributeId(), message.getModelName());
            
            boolean updated = schemaVisualizerService.toggleForeignKey(
                    message.getAttributeId()
            );
            
            if (updated) {
                WebSocketResponse<ToggleForeignKeyMessage> response = 
                    WebSocketResponse.success("TOGGLE_FOREIGN_KEY", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to toggle foreign key", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error toggling foreign key", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }

    @MessageMapping("/addAttribute")
    public void addAttribute(
            AddAttributeMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Adding attribute: {} ({}) to model: {}", 
                    message.getAttributeName(), message.getDataType(), message.getModelName());
            
            Long attributeId = schemaVisualizerService.addAttribute(
                    message.getModelId(),
                    message.getAttributeName(),
                    message.getDataType()
            );
            
            if (attributeId != null) {
                // Create response with real attribute ID
                AddAttributeResponseMessage responseMessage = new AddAttributeResponseMessage(
                    message.getModelName(),
                    message.getModelId(),
                    message.getAttributeName(),
                    message.getDataType(),
                    attributeId, // Real ID from database
                    sessionId
                );
                
                WebSocketResponse<AddAttributeResponseMessage> response = 
                    WebSocketResponse.success("ADD_ATTRIBUTE", responseMessage, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to add attribute", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error adding attribute", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }

    @MessageMapping("/deleteAttribute")
    public void deleteAttribute(
            DeleteAttributeMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            String sessionId = headerAccessor.getSessionId();
            message.setSessionId(sessionId);
            
            log.info("Deleting attribute ID: {} from model: {}", 
                    message.getAttributeId(), message.getModelName());
            
            boolean deleted = schemaVisualizerService.deleteAttribute(
                    message.getAttributeId()
            );
            
            if (deleted) {
                WebSocketResponse<DeleteAttributeMessage> response = 
                    WebSocketResponse.success("DELETE_ATTRIBUTE", message, sessionId);
                messagingTemplate.convertAndSend("/topic/schema-updates", response);
            } else {
                WebSocketResponse<String> errorResponse = 
                    WebSocketResponse.error("Failed to delete attribute", sessionId);
                messagingTemplate.convertAndSendToUser(
                    sessionId, "/queue/errors", errorResponse
                );
            }
            
        } catch (Exception e) {
            log.error("Error deleting attribute", e);
            String sessionId = headerAccessor.getSessionId();
            WebSocketResponse<String> errorResponse = 
                WebSocketResponse.error("Internal server error: " + e.getMessage(), sessionId);
            messagingTemplate.convertAndSendToUser(
                sessionId, "/queue/errors", errorResponse
            );
        }
    }
}