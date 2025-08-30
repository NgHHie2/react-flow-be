package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModelNameMessage implements BaseWebSocketMessage {
    private Long modelId;
    private String oldModelName;
    private String newModelName;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}