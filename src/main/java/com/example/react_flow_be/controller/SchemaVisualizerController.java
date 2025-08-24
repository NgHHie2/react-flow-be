package com.example.react_flow_be.controller;

import com.example.react_flow_be.dto.DatabaseDiagramDto;
import com.example.react_flow_be.service.SchemaVisualizerService;
import com.example.react_flow_be.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class SchemaVisualizerController {
    
    private final SchemaVisualizerService schemaVisualizerService;
    private final ConnectionRepository connectionRepository;
    private final AttributeRepository attributeRepository;
    private final ModelRepository modelRepository;
    private final DatabaseDiagramRepository databaseDiagramRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<DatabaseDiagramDto> getSchemaData() {
        try {
            log.info("Getting schema data");
            DatabaseDiagramDto response = schemaVisualizerService.getSchemaData();
            log.info("Retrieved schema data: {} models", response.getModels().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting schema data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeSampleData() {
        try {
            log.info("Initializing sample data");
            schemaVisualizerService.initializeSampleData();
            return ResponseEntity.ok("Sample data initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing sample data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Failed to initialize sample data: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            log.info("Health check requested");
            DatabaseDiagramDto schema = schemaVisualizerService.getSchemaData();
            return ResponseEntity.ok("Schema service is healthy - " + schema.getModels().size() + " models available");
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Schema service is unhealthy: " + e.getMessage());
        }
    }
    
    @PostMapping("/clear")
    @Transactional
    public ResponseEntity<String> clearAllData() {
        try {
            log.info("Clearing all data");
            
            // Delete in correct order to avoid foreign key constraints
            connectionRepository.deleteAll();
            log.info("Cleared all connections");
            
            attributeRepository.deleteAll();
            log.info("Cleared all attributes");
            
            modelRepository.deleteAll();
            log.info("Cleared all models");
            
            databaseDiagramRepository.deleteAll();
            log.info("Cleared all database diagrams");
            
            folderRepository.deleteAll();
            log.info("Cleared all folders");
            
            userRepository.deleteAll();
            log.info("Cleared all users");
            
            return ResponseEntity.ok("All data cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Failed to clear data: " + e.getMessage());
        }
    }
}