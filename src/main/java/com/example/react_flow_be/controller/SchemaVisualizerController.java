package com.example.react_flow_be.controller;

import com.example.react_flow_be.dto.SchemaVisualizerResponse;
import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SchemaVisualizerController {
    
    private final SchemaVisualizerService schemaVisualizerService;
    
    @GetMapping
    public ResponseEntity<SchemaVisualizerResponse> getSchemaData() {
        SchemaVisualizerResponse response = schemaVisualizerService.getSchemaData();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeSampleData() {
        schemaVisualizerService.initializeSampleData();
        return ResponseEntity.ok("Sample data initialized successfully");
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Schema Visualizer API is running");
    }
}