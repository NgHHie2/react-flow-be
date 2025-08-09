package com.example.react_flow_be.controller;

import com.example.react_flow_be.dto.DatabaseDiagramDto;
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
    public ResponseEntity<DatabaseDiagramDto> getSchemaData() {
        DatabaseDiagramDto response = schemaVisualizerService.getSchemaData();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeSampleData() {
        try {
            schemaVisualizerService.initializeSampleData();
            return ResponseEntity.ok("Sample data initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to initialize sample data: " + e.getMessage());
        }
    }
    
    @PostMapping("/clear")
    public ResponseEntity<String> clearAllData() {
        try {
            schemaVisualizerService.clearAllData();
            return ResponseEntity.ok("All data cleared successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to clear data: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Schema Visualizer API is running");
    }
}
