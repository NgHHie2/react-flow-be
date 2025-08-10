package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleForeignKeyMessage {
    private String modelName;
    private Long modelId;
    private Long attributeId;
    private String sessionId;
}