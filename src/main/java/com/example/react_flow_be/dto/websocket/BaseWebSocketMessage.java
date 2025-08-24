// src/main/java/com/example/react_flow_be/dto/websocket/BaseWebSocketMessage.java
package com.example.react_flow_be.dto.websocket;

/**
 * Base interface for all WebSocket messages
 */
public interface BaseWebSocketMessage {
    String getSessionId();
    void setSessionId(String sessionId);
    
    // Optional methods for message tracking
    default String getMessageId() { return null; }
    default void setMessageId(String messageId) { }
    default Long getClientTimestamp() { return null; }
    default void setClientTimestamp(Long timestamp) { }
}