package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddModelResponseMessage implements BaseWebSocketMessage {
    private String modelName;
    private Long realModelId;
    private String nodeId;
    private Double positionX;
    private Double positionY;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}