package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAttributeResponseMessage implements BaseWebSocketMessage {
    private String modelName;
    private Long modelId;
    private String attributeName;
    private String dataType;
    private Long realAttributeId;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}