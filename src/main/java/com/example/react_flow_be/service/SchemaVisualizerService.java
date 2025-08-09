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
    private final AttributeService attributeService;
    private final ConnectionService connectionService;
    private final DatabaseDiagramRepository databaseDiagramRepository;
    private final ModelRepository modelRepository;
    private final AttributeRepository attributeRepository;
    private final ConnectionRepository connectionRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    
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
        
        // Create sample data using other services
        User sampleUser = databaseDiagramService.createSampleUser();
        Folder sampleFolder = databaseDiagramService.createSampleFolder(sampleUser);
        DatabaseDiagram databaseDiagram = databaseDiagramService.createSampleDatabaseDiagram(sampleFolder);
        
        // Create Models
        Model userModel = modelService.createModel("User", 100.0, 300.0, true, databaseDiagram);
        Model postModel = modelService.createModel("Post", 500.0, 100.0, true, databaseDiagram);
        Model commentModel = modelService.createModel("Comment", 500.0, 500.0, false, databaseDiagram);
        
        // Create Attributes for User model
        attributeService.createAttribute(userModel, "id", "BIGINT", false, 0, false, true);
        attributeService.createAttribute(userModel, "username", "VARCHAR", false, 1, false, false);
        attributeService.createAttribute(userModel, "email", "VARCHAR", false, 2, false, false);
        attributeService.createAttribute(userModel, "password", "VARCHAR", false, 3, false, false);
        attributeService.createAttribute(userModel, "full_name", "VARCHAR", false, 4, true, false);
        attributeService.createAttribute(userModel, "created_at", "TIMESTAMP", false, 5, false, false);
        attributeService.createAttribute(userModel, "updated_at", "TIMESTAMP", false, 6, false, false);
        
        // Create Attributes for Post model
        attributeService.createAttribute(postModel, "id", "BIGINT", false, 0, false, true);
        attributeService.createAttribute(postModel, "title", "VARCHAR", false, 1, false, false);
        attributeService.createAttribute(postModel, "content", "TEXT", false, 2, true, false);
        attributeService.createAttribute(postModel, "user_id", "BIGINT", true, 3, false, false);
        attributeService.createAttribute(postModel, "status", "VARCHAR", false, 4, false, false);
        attributeService.createAttribute(postModel, "published_at", "TIMESTAMP", false, 5, true, false);
        attributeService.createAttribute(postModel, "created_at", "TIMESTAMP", false, 6, false, false);
        attributeService.createAttribute(postModel, "updated_at", "TIMESTAMP", false, 7, false, false);
        
        // Create Attributes for Comment model
        attributeService.createAttribute(commentModel, "id", "BIGINT", false, 0, false, true);
        attributeService.createAttribute(commentModel, "content", "TEXT", false, 1, false, false);
        attributeService.createAttribute(commentModel, "post_id", "BIGINT", true, 2, false, false);
        attributeService.createAttribute(commentModel, "user_id", "BIGINT", true, 3, false, false);
        attributeService.createAttribute(commentModel, "parent_comment_id", "BIGINT", true, 4, true, false);
        attributeService.createAttribute(commentModel, "status", "VARCHAR", false, 5, false, false);
        attributeService.createAttribute(commentModel, "created_at", "TIMESTAMP", false, 6, false, false);
        attributeService.createAttribute(commentModel, "updated_at", "TIMESTAMP", false, 7, false, false);
        
        // Create Connections - tạo sau khi có Attributes
        Attribute postUserIdAttribute = attributeService.getAttributeByModelAndName(postModel, "user_id");
        Attribute commentPostIdAttribute = attributeService.getAttributeByModelAndName(commentModel, "post_id");
        Attribute commentUserIdAttribute = attributeService.getAttributeByModelAndName(commentModel, "user_id");
        Attribute commentParentIdAttribute = attributeService.getAttributeByModelAndName(commentModel, "parent_comment_id");
        
        // Post -> User relationship
        if (postUserIdAttribute != null) {
            connectionService.createConnection(postUserIdAttribute, userModel, "id",
                "fk_post_user", "#4A90E2");
        }
        
        // Comment -> Post relationship
        if (commentPostIdAttribute != null) {
            connectionService.createConnection(commentPostIdAttribute, postModel, "id",
                "fk_comment_post", "#E53E3E");
        }
        
        // Comment -> User relationship
        if (commentUserIdAttribute != null) {
            connectionService.createConnection(commentUserIdAttribute, userModel, "id",
                "fk_comment_user", "#38A169");
        }
        
        // Comment -> Comment (self-reference) relationship
        if (commentParentIdAttribute != null) {
            connectionService.createConnection(commentParentIdAttribute, commentModel, "id",
                "fk_comment_parent", "#9CA3AF");
        }
    }

    @Transactional
    public void clearAllData() {
        try {
            // Clear in correct order to respect foreign key constraints
            System.out.println("🔄 Clearing connections...");
            connectionRepository.deleteAll();
            
            System.out.println("🔄 Clearing Attributes...");
            attributeRepository.deleteAll();
            
            System.out.println("🔄 Clearing models...");
            modelRepository.deleteAll();
            
            System.out.println("🔄 Clearing database diagrams...");
            databaseDiagramRepository.deleteAll();
            
            System.out.println("🔄 Clearing folders...");
            folderRepository.deleteAll();
            
            System.out.println("🔄 Clearing users...");
            userRepository.deleteAll();
            
            System.out.println("✅ All data cleared successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error clearing data: " + e.getMessage());
            throw new RuntimeException("Failed to clear data", e);
        }
    }
    
    @Transactional
    public boolean updateModelPosition(String modelName, Double positionX, Double positionY) {
        return modelService.updateModelPosition(modelName, positionX, positionY);
    }
    
    @Transactional
    public boolean updateAttribute(Long attributeId, String attributeName, String attributeType) {
        return attributeService.updateAttribute(attributeId, attributeName, attributeType);
    }
}
