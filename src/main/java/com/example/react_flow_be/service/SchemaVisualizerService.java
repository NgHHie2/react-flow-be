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
    
    @Transactional(readOnly = true)
    public SchemaVisualizerResponse getSchemaData() {
        List<Model> models = modelRepository.findAllWithFields();
        List<Connection> connections = connectionRepository.findAllWithModels();
        
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
        connectionRepository.deleteAll();
        fieldRepository.deleteAll(); 
        modelRepository.deleteAll();
        
        // Create Models với position
        Model userModel = createModel("User", 100.0, 100.0, true);
        Model postModel = createModel("Post", 500.0, 100.0, false);
        Model commentModel = createModel("Comment", 300.0, 400.0, true);
        
        modelRepository.saveAll(List.of(userModel, postModel, commentModel));
        
        // Create Fields cho User
        createField(userModel, "id", "number", false, 0, false, true);
        createField(userModel, "name", "string", false, 1, false, false);
        createField(userModel, "email", "string", false, 2, false, false);
        
        // Create Fields cho Post
        createField(postModel, "id", "number", false, 0, false, true);
        createField(postModel, "title", "string", false, 1, false, false);
        createField(postModel, "author", "User", true, 2, false, false);
        createField(postModel, "comments", "Comment[]", true, 3, true, false);
        createField(postModel, "createdAt", "Date", false, 4, false, false);
        
        // Create Fields cho Comment
        createField(commentModel, "id", "number", false, 0, false, true);
        createField(commentModel, "text", "string", false, 1, false, false);
        
        // Create Connections
        Connection postToUser = new Connection();
        postToUser.setName("author");
        postToUser.setSourceModel(postModel);
        postToUser.setTargetModel(userModel);
        postToUser.setConnectionType("MANY_TO_ONE");
        postToUser.setIsAnimated(true);
        
        Connection postToComment = new Connection();
        postToComment.setName("comments");
        postToComment.setSourceModel(postModel);
        postToComment.setTargetModel(commentModel);
        postToComment.setConnectionType("ONE_TO_MANY");
        postToComment.setIsAnimated(true);
        
        connectionRepository.saveAll(List.of(postToUser, postToComment));
    }
    
    private Model createModel(String name, Double x, Double y, Boolean isChild) {
        Model model = new Model();
        model.setName(name);
        model.setPositionX(x);
        model.setPositionY(y);
        model.setIsChild(isChild);
        model.setBackgroundColor("#3d5787");
        return model;
    }
    
    private void createField(Model model, String name, String type, boolean hasConnections, 
                           int order, boolean isNullable, boolean isPrimaryKey) {
        Field field = new Field();
        field.setName(name);
        field.setType(type);
        field.setHasConnections(hasConnections);
        field.setFieldOrder(order);
        field.setIsNullable(isNullable);
        field.setIsPrimaryKey(isPrimaryKey);
        field.setModel(model);
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
            model.getIsChild(),
            model.getBackgroundColor(),
            fieldDtos
        );
    }
    
    private FieldDto convertToFieldDto(Field field) {
        return new FieldDto(
            field.getId(),
            field.getName(),
            field.getType(),
            field.getHasConnections(),
            field.getFieldOrder(),
            field.getIsNullable(),
            field.getIsPrimaryKey()
        );
    }
    
    private ConnectionDto convertToConnectionDto(Connection connection) {
        return new ConnectionDto(
            connection.getId(),
            connection.getName(),
            connection.getConnectionType(),
            connection.getIsAnimated(),
            connection.getEdgeColor(),
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
            // log.error("Error updating model position for {}", modelName, e);
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
                field.setType(fieldType);
                
                // Cập nhật hasConnections nếu type thay đổi
                List<String> modelNames = modelRepository.findAll()
                    .stream()
                    .map(Model::getName)
                    .collect(Collectors.toList());
                
                boolean hasConnections = modelNames.stream()
                    .anyMatch(modelName -> fieldType.contains(modelName));
                field.setHasConnections(hasConnections);
                
                fieldRepository.save(field);
                return true;
            }
            return false;
        } catch (Exception e) {
            // log.error("Error updating field {}", fieldId, e);
            return false;
        }
    }
}
