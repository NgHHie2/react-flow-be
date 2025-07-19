package com.example.react_flow_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto {
    private Long id;
    private String name;
    private String type;
    private Boolean hasConnections;
    private Integer fieldOrder;
    private Boolean isNullable;
    private Boolean isPrimaryKey;
}