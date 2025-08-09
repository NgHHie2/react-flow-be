package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketResponse<T> {
    private String type; // "NODE_POSITION_UPDATE", "Attribute_UPDATE", "ERROR"
    private T data;
    private String sessionId;
    private long timestamp;
    
    public static <T> WebSocketResponse<T> success(String type, T data, String sessionId) {
        return new WebSocketResponse<>(type, data, sessionId, System.currentTimeMillis());
    }
    
    public static <T> WebSocketResponse<T> error(String message, String sessionId) {
        return new WebSocketResponse<>("ERROR", (T) message, sessionId, System.currentTimeMillis());
    }
}