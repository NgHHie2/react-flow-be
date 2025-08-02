package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.*;
import com.example.react_flow_be.entity.*;
import com.example.react_flow_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchemaVisualizerService {
    
    private final DatabaseDiagramService databaseDiagramService;
    private final ModelService modelService;
    private final FieldService fieldService;
    private final ConnectionService connectionService;
    
    @Transactional(readOnly = true)
    public DatabaseDiagramDto getSchemaData() {
        List<DatabaseDiagram> databaseDiagrams = databaseDiagramService.getAllDatabaseDiagrams();
        
        if (!databaseDiagrams.isEmpty()) {
            DatabaseDiagram databaseDiagram = databaseDiagrams.get(0);
            
            List<ModelDto> modelDtos = databaseDiagram.getModels().stream()
                .map(modelService::convertToModelDto)
                .collect(Collectors.toList());
            
            return new DatabaseDiagramDto(
                databaseDiagram.getId(),
                databaseDiagram.getName(),
                databaseDiagram.getDescription(),
                databaseDiagram.getDatabaseType().name(),
                databaseDiagram.getVersion(),
                databaseDiagram.getCharset(),
                databaseDiagram.getCollation(),
                databaseDiagram.getIsPublic(),
                databaseDiagram.getIsTemplate(),
                databaseDiagram.getZoomLevel(),
                databaseDiagram.getPanX(),
                databaseDiagram.getPanY(),
                modelDtos
            );
        } else {
            return new DatabaseDiagramDto(
                null, "Empty Diagram", "No data available", 
                "MYSQL", "8.0", "utf8mb4", "utf8mb4_unicode_ci",
                false, false, 1.0, 0.0, 0.0, List.of()
            );
        }
    }
    
    @Transactional
    public void initializeSampleData() {
        // Clear existing data
        // ... cleanup code
        
        // Create sample data using other services
        User sampleUser = databaseDiagramService.createSampleUser();
        Folder sampleFolder = databaseDiagramService.createSampleFolder(sampleUser);
        DatabaseDiagram databaseDiagram = databaseDiagramService.createSampleDatabaseDiagram(sampleFolder);
        
        // Create Models
        Model userModel = modelService.createModel("User", 100.0, 300.0, true, databaseDiagram);
        Model postModel = modelService.createModel("Post", 500.0, 100.0, true, databaseDiagram);
        Model commentModel = modelService.createModel("Comment", 500.0, 500.0, false, databaseDiagram);
        
        // Create Fields
        fieldService.createField(userModel, "id", "BIGINT", false, 0, false, true);
        fieldService.createField(userModel, "username", "VARCHAR", false, 1, false, false);
        // ... more fields
        
        // Create Connections - cần tạo sau khi có fields
        Field postUserIdField = fieldService.getFieldByModelAndName(postModel, "user_id");
        Field commentPostIdField = fieldService.getFieldByModelAndName(commentModel, "post_id");
        Field commentUserIdField = fieldService.getFieldByModelAndName(commentModel, "user_id");
        
        if (postUserIdField != null) {
            connectionService.createConnection(postUserIdField, userModel, "id",
                Connection.ConnectionType.MANY_TO_ONE, "fk_post_user", "#4A90E2");
        }
        
        if (commentPostIdField != null) {
            connectionService.createConnection(commentPostIdField, postModel, "id",
                Connection.ConnectionType.MANY_TO_ONE, "fk_comment_post", "#E53E3E");
        }
        
        if (commentUserIdField != null) {
            connectionService.createConnection(commentUserIdField, userModel, "id",
                Connection.ConnectionType.MANY_TO_ONE, "fk_comment_user", "#38A169");
        }
    }
    
    @Transactional
    public boolean updateModelPosition(String modelName, Double positionX, Double positionY) {
        return modelService.updateModelPosition(modelName, positionX, positionY);
    }
    
    @Transactional
    public boolean updateField(Long fieldId, String fieldName, String fieldType) {
        return fieldService.updateField(fieldId, fieldName, fieldType);
    }
}
