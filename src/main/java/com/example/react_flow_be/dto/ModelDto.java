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
    private String name;
    private Double positionX;
    private Double positionY;
    private Boolean isChild;
    private String backgroundColor;
    private List<FieldDto> fields;
}