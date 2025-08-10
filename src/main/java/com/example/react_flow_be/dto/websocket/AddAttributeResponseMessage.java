// src/main/java/com/example/react_flow_be/dto/websocket/AddAttributeResponseMessage.java
package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAttributeResponseMessage {
    private String modelName;
    private Long modelId;
    private String attributeName;
    private String dataType;
    private Long realAttributeId; // Real ID from database
    private String sessionId;
}