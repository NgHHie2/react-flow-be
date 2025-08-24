package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelUpdateMessage implements BaseWebSocketMessage {
    private String nodeId;
    private String modelName;
    private Double positionX;
    private Double positionY;
    private Double width;
    private Double height;
    private String backgroundColor;
    private String sessionId;
    private Long modelId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}