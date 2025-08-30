package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteModelMessage implements BaseWebSocketMessage {
    private Long modelId;
    private String modelName;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}
