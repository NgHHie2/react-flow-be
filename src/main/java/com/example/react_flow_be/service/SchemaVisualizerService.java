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
    private final ConnectionRepository connectionRepository; // This is now for ModelConnection → Connection
    private final DatabaseRepository databaseRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public SchemaVisualizerResponse getSchemaData() {
        List<Model> models = modelRepository.findAllWithFields();
        List<Connection> connections = connectionRepository.findAllWithModels(); // Connection = old ModelConnection
        
        List<ModelDto> modelDtos = models.stream()
            .map(this::convertToModelDto)
            .collect(Collectors.toList());
            
        List<ConnectionDto> connectionDtos = connections.stream()
            .map(this::convertToConnectionDto)
            .collect(Collectors.toList());
            
        return new SchemaVisualizerResponse(modelDtos, connectionDtos);
    }
    
    @Transactional
    public void initializeSampleData() {
        // Clear existing data
        connectionRepository.deleteAll(); // Clear connections (old ModelConnection)
        fieldRepository.deleteAll(); 
        modelRepository.deleteAll();
        databaseRepository.deleteAll();
        
        // Create sample user and folder first
        User sampleUser = createSampleUser();
        Folder sampleFolder = createSampleFolder(sampleUser);
        
        // Create Database (extends Diagram)
        Database database = createSampleDatabase(sampleFolder);
        
        // Create Models (extends Shape) với position
        Model userModel = createModel("User", 100.0, 100.0, true, database);
        Model postModel = createModel("Post", 500.0, 100.0, false, database);
        Model commentModel = createModel("Comment", 300.0, 400.0, true, database);
        
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
        
        // Create Connections (old ModelConnection - now Connection entity)
        Connection postToUser = new Connection();
        postToUser.setLabel("author");
        postToUser.setSourceModel(postModel);
        postToUser.setTargetModel(userModel);
        postToUser.setConnectionType(Connection.ConnectionType.MANY_TO_ONE);
        postToUser.setSourceFieldName("user_id");
        postToUser.setTargetFieldName("id");
        postToUser.setForeignKeyName("fk_post_user");
        postToUser.setIsAnimated(true);
        postToUser.setStrokeColor("#2563eb");
        postToUser.setTargetArrowType(Connection.ArrowType.CROW_FOOT);
        
        Connection commentToPost = new Connection();
        commentToPost.setLabel("post");
        commentToPost.setSourceModel(commentModel);
        commentToPost.setTargetModel(postModel);
        commentToPost.setConnectionType(Connection.ConnectionType.MANY_TO_ONE);
        commentToPost.setSourceFieldName("post_id");
        commentToPost.setTargetFieldName("id");
        commentToPost.setForeignKeyName("fk_comment_post");
        commentToPost.setIsAnimated(true);
        commentToPost.setStrokeColor("#dc2626");
        commentToPost.setTargetArrowType(Connection.ArrowType.CROW_FOOT);
        
        Connection commentToUser = new Connection();
        commentToUser.setLabel("author");
        commentToUser.setSourceModel(commentModel);
        commentToUser.setTargetModel(userModel);
        commentToUser.setConnectionType(Connection.ConnectionType.MANY_TO_ONE);
        commentToUser.setSourceFieldName("user_id");
        commentToUser.setTargetFieldName("id");
        commentToUser.setForeignKeyName("fk_comment_user");
        commentToUser.setIsAnimated(true);
        commentToUser.setStrokeColor("#16a34a");
        commentToUser.setTargetArrowType(Connection.ArrowType.CROW_FOOT);
        
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
        
        // Model properties (specialized)
        model.setModelType(Model.ModelType.TABLE);
        model.setDatabase(database);
        model.setDiagram(database); // Database extends Diagram
        
        return model;
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
            connection.getSourceModel().getName(),
            connection.getTargetModel().getName()
        );
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
                            field.setPrecision(Integer.parseInt(parts[0].trim()));
                            field.setScale(Integer.parseInt(parts[1].trim()));
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