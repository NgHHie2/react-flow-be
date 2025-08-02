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
    private String dataType;
    private Integer length;
    private Integer precision;
    private Integer scale;
    private Boolean isNullable;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;
    private Boolean isUnique;
    private Boolean isAutoIncrement;
    private String defaultValue;
    private String comment;
    private Integer fieldOrder;
    private Boolean hasIndex;
    private String indexName;
    private String indexType;
    
    // Connection info if field is FK
    private ConnectionDto connection;
}