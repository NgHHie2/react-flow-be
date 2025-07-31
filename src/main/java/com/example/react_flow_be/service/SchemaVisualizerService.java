package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.*;
import com.example.react_flow_be.entity.*;
import com.example.react_flow_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchemaVisualizerService {
    
    private final ModelRepository modelRepository;
    private final FieldRepository fieldRepository;
    private final ConnectionRepository connectionRepository;
    private final DatabaseRepository databaseRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public SchemaVisualizerResponse getSchemaData() {
        // Get all databases
        List<Database> databases = databaseRepository.findAll();
        
        List<ModelDto> modelDtos;
        List<ConnectionDto> connectionDtos;
        
        if (!databases.isEmpty()) {
            Database database = databases.get(0); // Get first database
            
            // Get models từ shapes của database (simplified approach)
            List<Model> models = database.getModels();
            
            // Get connections từ diagram ID thay vì relationships
            List<Connection> connections = connectionRepository.findByDiagramId(database.getId());
            
            modelDtos = models.stream()
                .map(this::convertToModelDto)
                .collect(Collectors.toList());
                
            connectionDtos = connections.stream()
                .map(connection -> new ConnectionDto(
                    connection.getId(),
                    connection.getSourceFieldName(), // Dùng field name làm connection name
                    connection.getConnectionType().name(),
                    connection.getIsAnimated(),
                    connection.getStrokeColor(),
                    getModelNameByNodeId(connection.getSourceNodeId()), // Convert nodeId → model name
                    getModelNameByNodeId(connection.getTargetNodeId())  // Convert nodeId → model name
                ))
                .collect(Collectors.toList());
        } else {
            modelDtos = List.of();
            connectionDtos = List.of();
        }
        
        return new SchemaVisualizerResponse(modelDtos, connectionDtos);
    }
    
    @Transactional
    public void initializeSampleData() {
        // Clear existing data
        connectionRepository.deleteAll();
        fieldRepository.deleteAll(); 
        modelRepository.deleteAll();
        databaseRepository.deleteAll();
        
        // Create sample user and folder first
        User sampleUser = createSampleUser();
        Folder sampleFolder = createSampleFolder(sampleUser);
        
        // Create Database
        Database database = createSampleDatabase(sampleFolder);
        
        // Create Models với better positioning
        Model userModel = createModel("User", 100.0, 300.0, true, database);
        Model postModel = createModel("Post", 500.0, 100.0, true, database);
        Model commentModel = createModel("Comment", 500.0, 500.0, false, database);
        
        // Save models
        modelRepository.saveAll(List.of(userModel, postModel, commentModel));
        
        // Create Fields cho User
        createField(userModel, "id", "BIGINT", false, 0, false, true);
        createField(userModel, "username", "VARCHAR", false, 1, false, false);
        createField(userModel, "email", "VARCHAR", false, 2, false, false);
        createField(userModel, "created_at", "TIMESTAMP", false, 3, false, false);
        
        // Create Fields cho Post
        createField(postModel, "id", "BIGINT", false, 0, false, true);
        createField(postModel, "title", "VARCHAR", false, 1, false, false);
        createField(postModel, "content", "TEXT", false, 2, true, false);
        createField(postModel, "user_id", "BIGINT", true, 3, false, false); // FK
        createField(postModel, "created_at", "TIMESTAMP", false, 4, false, false);
        
        // Create Fields cho Comment
        createField(commentModel, "id", "BIGINT", false, 0, false, true);
        createField(commentModel, "content", "TEXT", false, 1, false, false);
        createField(commentModel, "post_id", "BIGINT", true, 2, false, false); // FK
        createField(commentModel, "user_id", "BIGINT", true, 3, false, false); // FK
        createField(commentModel, "created_at", "TIMESTAMP", false, 4, false, false);
        
        // Create Connections với better colors và cleaner approach
        Connection postToUser = createConnection(
            "author", postModel.getNodeId(), userModel.getNodeId(),
            Connection.ConnectionType.MANY_TO_ONE, "user_id", "id",
            "fk_post_user", "#4A90E2", database
        );
        
        Connection commentToPost = createConnection(
            "post", commentModel.getNodeId(), postModel.getNodeId(),
            Connection.ConnectionType.MANY_TO_ONE, "post_id", "id", 
            "fk_comment_post", "#E53E3E", database
        );
        
        Connection commentToUser = createConnection(
            "author", commentModel.getNodeId(), userModel.getNodeId(),
            Connection.ConnectionType.MANY_TO_ONE, "user_id", "id",
            "fk_comment_user", "#38A169", database
        );
        
        connectionRepository.saveAll(List.of(postToUser, commentToPost, commentToUser));
    }
    
    private User createSampleUser() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setFullName("System Administrator");
        user.setRole(User.UserRole.ADMIN);
        return userRepository.save(user);
    }
    
    private Folder createSampleFolder(User owner) {
        Folder folder = new Folder();
        folder.setName("Sample ER Diagrams");
        folder.setDescription("Sample folder containing ER diagram examples");
        folder.setOwner(owner);
        folder.setColor("#3d5787");
        return folderRepository.save(folder);
    }
    
    private Database createSampleDatabase(Folder folder) {
        Database database = new Database();
        database.setName("Blog System");
        database.setDescription("Sample blog system database schema");
        database.setType(Diagram.DiagramType.ER_DIAGRAM);
        database.setDatabaseType(Database.DatabaseType.MYSQL);
        database.setVersion("8.0");
        database.setCharset("utf8mb4");
        database.setCollation("utf8mb4_unicode_ci");
        database.setFolder(folder);
        database.setIsPublic(false);
        database.setIsTemplate(true);
        database.setZoomLevel(1.0);
        database.setPanX(0.0);
        database.setPanY(0.0);
        return databaseRepository.save(database);
    }
    
    private Model createModel(String name, Double x, Double y, Boolean isChild, Database database) {
        Model model = new Model();
        
        // Shape properties (inherited)
        model.setNodeId("model_" + name.toLowerCase());
        model.setName(name);
        model.setType(Shape.ShapeType.TABLE);
        model.setPositionX(x);
        model.setPositionY(y);
        model.setWidth(200.0);
        model.setHeight(120.0);
        model.setBackgroundColor(isChild ? "#f1f5f9" : "#ffffff");
        model.setBorderColor("#e2e8f0");
        model.setBorderWidth(2);
        model.setBorderRadius(8);
        model.setZIndex(1);
        
        // Model properties
        model.setModelType(Model.ModelType.TABLE);
        model.setDiagram(database); // Set diagram instead of database
        
        return model;
    }
    
    private Connection createConnection(String label, String sourceNodeId, String targetNodeId,
                                     Connection.ConnectionType connectionType, String sourceField, 
                                     String targetField, String fkName, String color, Database database) {
        Connection connection = new Connection();
        
        // Link properties (inherited)
        connection.setEdgeId("conn_" + sourceNodeId + "_to_" + targetNodeId);
        connection.setLabel(label);
        connection.setSourceNodeId(sourceNodeId);
        connection.setTargetNodeId(targetNodeId);
        connection.setSourceNodeType(Link.NodeType.SHAPE);
        connection.setTargetNodeType(Link.NodeType.SHAPE);
        
        // Set handles để connect tới đúng field
        connection.setSourceHandle(sourceNodeId + "-" + sourceField); // Post-user_id
        connection.setTargetHandle(targetNodeId); // User (target model)
        
        connection.setStrokeColor(color);
        connection.setStrokeWidth(2);
        connection.setIsAnimated(true);
        connection.setTargetArrowType(Link.ArrowType.CROW_FOOT);
        connection.setDiagram(database); // Set diagram
        
        // Connection properties
        connection.setConnectionType(connectionType);
        connection.setSourceFieldName(sourceField);
        connection.setTargetFieldName(targetField);
        connection.setForeignKeyName(fkName);
        
        return connection;
    }
    
    private void createField(Model model, String name, String dataType, boolean hasConnections, 
                           int order, boolean isNullable, boolean isPrimaryKey) {
        Field field = new Field();
        field.setName(name);
        field.setDataType(dataType);
        field.setHasConnections(hasConnections);
        field.setFieldOrder(order);
        field.setIsNullable(isNullable);
        field.setIsPrimaryKey(isPrimaryKey);
        field.setIsForeignKey(hasConnections);
        field.setModel(model);
        
        // Set length based on data type
        if ("VARCHAR".equals(dataType)) {
            field.setLength(255);
        } else if ("BIGINT".equals(dataType)) {
            field.setLength(20);
        }
        
        // Set index information
        if (isPrimaryKey) {
            field.setHasIndex(true);
            field.setIndexName("PRIMARY");
            field.setIndexType(Field.IndexType.PRIMARY);
        } else if (hasConnections) {
            field.setHasIndex(true);
            field.setIndexName("idx_" + model.getName().toLowerCase() + "_" + name);
            field.setIndexType(Field.IndexType.INDEX);
        }
        
        fieldRepository.save(field);
    }
    
    private ModelDto convertToModelDto(Model model) {
        List<FieldDto> fieldDtos = model.getFields().stream()
            .map(this::convertToFieldDto)
            .sorted((f1, f2) -> f1.getFieldOrder().compareTo(f2.getFieldOrder()))
            .collect(Collectors.toList());
            
        return new ModelDto(
            model.getId(),
            model.getName(),
            model.getPositionX(),
            model.getPositionY(),
            model.getBackgroundColor().equals("#f1f5f9"), // isChild based on background color
            model.getBackgroundColor(),
            fieldDtos
        );
    }
    
    private FieldDto convertToFieldDto(Field field) {
        return new FieldDto(
            field.getId(),
            field.getName(),
            field.getDataType() + (field.getLength() != null ? "(" + field.getLength() + ")" : ""),
            field.getHasConnections(),
            field.getFieldOrder(),
            field.getIsNullable(),
            field.getIsPrimaryKey()
        );
    }
    
    private ConnectionDto convertToConnectionDto(Connection connection) {
        return new ConnectionDto(
            connection.getId(),
            connection.getLabel(),
            connection.getConnectionType().name(),
            connection.getIsAnimated(),
            connection.getStrokeColor(),
            connection.getSourceNodeId(), // Dùng sourceNodeId thay vì model name
            connection.getTargetNodeId()  // Dùng targetNodeId thay vì model name
        );
    }
    
    // Helper method để get Model name by nodeId
    private String getModelNameByNodeId(String nodeId) {
        // Query từ Model repository để get name by nodeId
        return modelRepository.findAll().stream()
            .filter(model -> nodeId.equals(model.getNodeId()))
            .map(Model::getName)
            .findFirst()
            .orElse("Unknown");
    }

    @Transactional
    public boolean updateModelPosition(String modelName, Double positionX, Double positionY) {
        try {
            Optional<Model> modelOpt = modelRepository.findByName(modelName);
            if (modelOpt.isPresent()) {
                Model model = modelOpt.get();
                model.setPositionX(positionX);
                model.setPositionY(positionY);
                modelRepository.save(model);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateField(Long fieldId, String fieldName, String fieldType) {
        try {
            Optional<Field> fieldOpt = fieldRepository.findById(fieldId);
            if (fieldOpt.isPresent()) {
                Field field = fieldOpt.get();
                field.setName(fieldName);
                
                // Parse dataType and length from fieldType (e.g., "VARCHAR(255)")
                if (fieldType.contains("(")) {
                    String dataType = fieldType.substring(0, fieldType.indexOf("("));
                    String lengthStr = fieldType.substring(fieldType.indexOf("(") + 1, fieldType.indexOf(")"));
                    field.setDataType(dataType);
                    try {
                        field.setLength(Integer.parseInt(lengthStr));
                    } catch (NumberFormatException e) {
                        // Handle cases like DECIMAL(10,2)
                        if (lengthStr.contains(",")) {
                            String[] parts = lengthStr.split(",");
                            try {
                                field.setPrecision(Integer.parseInt(parts[0].trim()));
                                field.setScale(Integer.parseInt(parts[1].trim()));
                            } catch (NumberFormatException ex) {
                                // Ignore if can't parse precision/scale
                            }
                        }
                    }
                } else {
                    field.setDataType(fieldType);
                }
                
                // Update hasConnections based on field name patterns
                boolean hasConnections = fieldName.endsWith("_id") && !field.getIsPrimaryKey();
                field.setHasConnections(hasConnections);
                field.setIsForeignKey(hasConnections);
                
                fieldRepository.save(field);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}