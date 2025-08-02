package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.ModelDto;
import com.example.react_flow_be.entity.*;
import com.example.react_flow_be.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModelService {
    
    private final ModelRepository modelRepository;
    private final FieldService fieldService;
    
    @Transactional
    public boolean updateModelPosition(String nodeId, Double positionX, Double positionY) {
        Optional<Model> modelOpt = modelRepository.findByNodeId(nodeId);
        if (modelOpt.isPresent()) {
            Model model = modelOpt.get();
            model.setPositionX(positionX);
            model.setPositionY(positionY);
            modelRepository.save(model);
            return true;
        }
        return false;
    }
    
    @Transactional
    public Model createModel(String name, Double x, Double y, Boolean isChild, DatabaseDiagram databaseDiagram) {
        Model model = new Model();
        
        model.setNodeId("model_" + name.toLowerCase());
        model.setName(name);
        model.setPositionX(x);
        model.setPositionY(y);
        model.setWidth(200.0);
        model.setHeight(120.0);
        model.setBackgroundColor(isChild ? "#f1f5f9" : "#ffffff");
        model.setBorderColor("#e2e8f0");
        model.setBorderWidth(2);
        model.setBorderRadius(8);
        model.setZIndex(1);
        model.setModelType(Model.ModelType.TABLE);
        model.setDatabaseDiagram(databaseDiagram);
        
        return modelRepository.save(model);
    }
    
    public ModelDto convertToModelDto(Model model) {
        return new ModelDto(
            model.getId(),
            model.getNodeId(),
            model.getName(),
            model.getModelType().name(),
            model.getPositionX(),
            model.getPositionY(),
            model.getWidth(),
            model.getHeight(),
            model.getBackgroundColor(),
            model.getBorderColor(),
            model.getBorderWidth(),
            model.getBorderStyle(),
            model.getBorderRadius(),
            model.getZIndex(),
            model.getRotation(),
            fieldService.convertToDtoList(model.getFields())
        );
    }
    
    public String getModelNameByNodeId(String nodeId) {
        return modelRepository.findAll().stream()
            .filter(model -> nodeId.equals(model.getNodeId()))
            .map(Model::getName)
            .findFirst()
            .orElse("Unknown");
    }
}
