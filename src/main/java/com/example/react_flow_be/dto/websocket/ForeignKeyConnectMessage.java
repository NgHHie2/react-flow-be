package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForeignKeyConnectMessage implements BaseWebSocketMessage {
    private Long attributeId;
    private String targetModelName;
    private String targetAttributeName;
    private Long targetAttributeId;
    private String foreignKeyName;
    private String sessionId;
    
    // Message tracking fields
    private String messageId;
    private Long clientTimestamp;
}