package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddModelMessage implements BaseWebSocketMessage {
    private String modelName;
    private Double positionX;
    private Double positionY;
    private Long databaseDiagramId;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}
