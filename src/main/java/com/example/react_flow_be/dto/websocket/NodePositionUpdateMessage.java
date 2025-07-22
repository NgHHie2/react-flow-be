package com.example.react_flow_be.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodePositionUpdateMessage {
    private String modelName;
    private Double positionX;
    private Double positionY;
    private String sessionId; // để tránh broadcast lại cho chính người gửi
}
