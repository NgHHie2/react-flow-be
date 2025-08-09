package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeUpdateMessage {
    private Long attributeId;
    private String attributeName;
    private String attributeType;
    private String modelName;
    private String nodeId;
    private Boolean isNullable;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;
    private String sessionId;
}
