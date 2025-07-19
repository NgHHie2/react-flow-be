package com.example.react_flow_be.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemaVisualizerResponse {
    private List<ModelDto> models;
    private List<ConnectionDto> connections;
}