// src/main/java/com/example/react_flow_be/controller/SchemaWebSocketController.java
package com.example.react_flow_be.controller;

import com.example.react_flow_be.config.WebSocketSessionManager;
import com.example.react_flow_be.dto.websocket.*;
import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Set;
import java.util.function.Supplier;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SchemaWebSocketController {
    
    private final SchemaVisualizerService schemaVisualizerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @MessageMapping("/updateNodePosition")
    public void updateNodePosition(ModelUpdateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "NODE_POSITION_UPDATE",
            message,
            headerAccessor,
            () -> schemaVisualizerService.updateModelPosition(
                message.getModelId(), 
                message.getPositionX(), 
                message.getPositionY()
            )
        );
    }
    
    @MessageMapping("/updateAttribute")
    public void updateAttribute(AttributeUpdateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "FIELD_UPDATE",
            message,
            headerAccessor,
            () -> schemaVisualizerService.updateAttribute(
                message.getAttributeId(),
                message.getAttributeName(),
                message.getAttributeType()
            )
        );
    }

    @MessageMapping("/togglePrimaryKey")
    public void togglePrimaryKey(TogglePrimaryKeyMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "TOGGLE_PRIMARY_KEY",
            message,
            headerAccessor,
            () -> schemaVisualizerService.togglePrimaryKey(message.getAttributeId())
        );
    }

    @MessageMapping("/toggleForeignKey")
    public void toggleForeignKey(ToggleForeignKeyMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "TOGGLE_FOREIGN_KEY",
            message,
            headerAccessor,
            () -> schemaVisualizerService.toggleForeignKey(message.getAttributeId())
        );
    }

    @MessageMapping("/addAttribute")
    public void addAttribute(AddAttributeMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "ADD_ATTRIBUTE",
            message,
            headerAccessor,
            () -> {
                Long attributeId = schemaVisualizerService.addAttribute(
                    message.getModelId(),
                    message.getAttributeName(),
                    message.getDataType()
                );
                
                if (attributeId != null) {
                    // Tạo response với đầy đủ thông tin attribute
                    return new AddAttributeResponseMessage(
                        message.getModelName(),
                        message.getModelId(),
                        message.getAttributeName(),
                        message.getDataType(),
                        attributeId, // realAttributeId
                        message.getSessionId(),
                        message.getMessageId(),
                        message.getClientTimestamp()
                    );
                }
                return null;
            }
        );
    }

    @MessageMapping("/deleteAttribute")
    public void deleteAttribute(DeleteAttributeMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "DELETE_ATTRIBUTE",
            message,
            headerAccessor,
            () -> {
                schemaVisualizerService.deleteAttribute(message.getAttributeId());
                return new DeleteAttributeMessage(
                    message.getModelName(),message.getModelId(),
                        message.getAttributeId(), null, null, null
                    );
            }
        );
    }

    @MessageMapping("/connectForeignKey")
    public void connectForeignKey(ForeignKeyConnectMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received FK connect request: attributeId={}, target={}.{}", 
            message.getAttributeId(), message.getTargetModelName(), message.getTargetAttributeName());
        
        handleWebSocketMessage(
            "FOREIGN_KEY_CONNECT",
            message,
            headerAccessor,
            () -> schemaVisualizerService.createForeignKeyConnection(
                message.getAttributeId(),
                message.getTargetModelName(),
                message.getTargetAttributeName(),
                message.getTargetAttributeId(),
                message.getForeignKeyName()
            )
        );
    }

    @MessageMapping("/disconnectForeignKey")
    public void disconnectForeignKey(ForeignKeyDisconnectMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received FK disconnect request: attributeId={}", message.getAttributeId());
        
        handleWebSocketMessage(
            "FOREIGN_KEY_DISCONNECT",
            message,
            headerAccessor,
            () -> schemaVisualizerService.removeForeignKeyConnection(message.getAttributeId())
        );
    }

   @MessageMapping("/addModel")
    public void addModel(AddModelMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "ADD_MODEL",
            message,
            headerAccessor,
            () -> {
                Long modelId = schemaVisualizerService.addModel(
                    message.getDatabaseDiagramId(),
                    message.getModelName(),
                    message.getPositionX(),
                    message.getPositionY()
                );
                
                if (modelId != null) {
                    // Tạo response với đầy đủ thông tin model
                    return new AddModelResponseMessage(
                        message.getModelName(),
                        modelId, // realModelId
                        message.getModelName(), // nodeId = modelName
                        message.getPositionX(),
                        message.getPositionY(),
                        message.getSessionId(),
                        message.getMessageId(),
                        message.getClientTimestamp()
                    );
                }
                return null;
            }
        );
    }

    @MessageMapping("/updateModelName")
    public void updateModelName(UpdateModelNameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "UPDATE_MODEL_NAME",
            message,
            headerAccessor,
            () -> schemaVisualizerService.updateModelName(
                message.getModelId(),
                message.getNewModelName()
            )
        );
    }

    @MessageMapping("/deleteModel")
    public void deleteModel(DeleteModelMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleWebSocketMessage(
            "DELETE_MODEL",
            message,
            headerAccessor,
            () -> {
                boolean success = schemaVisualizerService.deleteModel(message.getModelId());
                if (success) {
                    // Trả về message với cả modelId và modelName để frontend xử lý
                    return new DeleteModelMessage(
                        message.getModelId(),
                        message.getModelName(), 
                        message.getSessionId(),
                        message.getMessageId(),
                        message.getClientTimestamp()
                    );
                }
                return null;
            }
        );
    }

    /**
     * Generic method to handle WebSocket messages with filtering support
     */
    private <T, R> void handleWebSocketMessage(
            String messageType,
            T message,
            SimpMessageHeaderAccessor headerAccessor,
            Supplier<R> serviceCall
    ) {
        String sessionId = headerAccessor.getSessionId();

        log.info("Current session: {}", sessionId);
        log.info("All active sessions: {}", sessionManager.getAllSessions());
        
        try {
            if (message instanceof BaseWebSocketMessage) {
                ((BaseWebSocketMessage) message).setSessionId(sessionId);
            }
            
            log.info("Processing {}: {}", messageType, message);
            
            R result = serviceCall.get();
            boolean success = isSuccessfulResult(result);
            
            if (success) {
                Object responseData = (result != null && !(result instanceof Boolean)) ? result : message;
                String messageId = extractMessageId(message);
                
                WebSocketResponse<Object> response;
                if (messageId != null) {
                    response = WebSocketResponse.successWithTracking(messageType, responseData, sessionId, messageId);
                } else {
                    response = WebSocketResponse.success(messageType, responseData, sessionId);
                }
                
                // Gửi cho tất cả sessions trừ sender
                Set<String> otherSessions = sessionManager.getActiveSessionsExcept(sessionId);
                System.out.println(otherSessions.size());
                for (String otherSessionId : otherSessions) {
                    System.out.println("other: " + otherSessionId);
                    messagingTemplate.convertAndSendToUser(
                        otherSessionId, 
                        "/queue/schema-updates", 
                        response
                    );
                }
                
                log.info("Successfully broadcasted {} to {} other sessions", messageType, otherSessions.size());
            } else {
                sendErrorToUser(sessionId, "Failed to process " + messageType.toLowerCase().replace("_", " "));
            }
            
        } catch (Exception e) {
            log.error("Error processing {}: {}", messageType, e.getMessage(), e);
            sendErrorToUser(sessionId, "Internal server error: " + e.getMessage());
        }
    }
    
    /**
     * Extract messageId from message if available (for client-side filtering)
     */
    private String extractMessageId(Object message) {
        if (message instanceof BaseWebSocketMessage) {
            return ((BaseWebSocketMessage) message).getMessageId();
        }
        return null;
    }
    
    /**
     * Check if the service call result indicates success
     */
    private boolean isSuccessfulResult(Object result) {
        if (result == null) return false;
        if (result instanceof Boolean) return (Boolean) result;
        return true;
    }
    
    /**
     * Send error message to specific user
     */
    private void sendErrorToUser(String sessionId, String errorMessage) {
        WebSocketResponse<String> errorResponse = 
            WebSocketResponse.error(errorMessage, sessionId);
        messagingTemplate.convertAndSendToUser(
            sessionId, "/queue/errors", errorResponse
        );
    }
}