package com.example.react_flow_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionDto {
    private Long id;
    private String connectionType;
    private String targetModelName;
    private String targetFieldName;
    private String foreignKeyName;
    private String onUpdate;
    private String onDelete;
    private Boolean isEnforced;
    
    // Visual properties
    private String strokeColor;
    private Integer strokeWidth;
    private String strokeStyle;
    private Boolean isAnimated;
    private String sourceArrowType;
    private String targetArrowType;
}