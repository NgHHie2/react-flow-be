package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldUpdateMessage {
    private Long fieldId;
    private String fieldName;
    private String fieldType;
    private String modelName;
    private String sessionId;
}