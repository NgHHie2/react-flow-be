package com.example.react_flow_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionDto {
    private Long id;
    private String name;
    private String connectionType;
    private Boolean isAnimated;
    private String edgeColor;
    private String source; // Model name
    private String target; // Model name
}