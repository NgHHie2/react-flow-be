package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAttributeMessage {
    private String modelName;
    private Long modelId;
    private String attributeName;
    private String dataType;
    private String sessionId;
}
