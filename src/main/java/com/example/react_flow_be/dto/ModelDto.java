package com.example.react_flow_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {
    private Long id;
    private String nodeId;
    private String name;
    private String modelType;
    
    // Position & Size
    private Double positionX;
    private Double positionY;
    private Double width;
    private Double height;
    
    // Style
    private String backgroundColor;
    private String borderColor;
    private Integer borderWidth;
    private String borderStyle;
    private Integer borderRadius;
    private Integer zIndex;
    private Double rotation;
    
    // Fields with their connections
    private List<FieldDto> fields;
}
