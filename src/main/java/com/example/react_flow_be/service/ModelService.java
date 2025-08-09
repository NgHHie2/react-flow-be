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
    private final AttributeService attributeService;
    
    @Transactional
    public boolean updateModelPosition(String nodeId, Double positionX, Double positionY, Long diagramId) {
        Optional<Model> modelOpt = modelRepository.findByNodeIdAndDatabaseDiagram_Id(nodeId, diagramId);
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
        
        model.setNodeId(name);
        model.setName(name);
        model.setPositionX(x);
        model.setPositionY(y);
        model.setDatabaseDiagram(databaseDiagram);
        
        return modelRepository.save(model);
    }
    
    public ModelDto convertToModelDto(Model model) {
        return new ModelDto(
            model.getId(),
            model.getNodeId(),
            model.getName(),
            model.getPositionX(),
            model.getPositionY(),
            attributeService.convertToDtoList(model.getAttributes())
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
