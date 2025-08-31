// src/main/java/com/example/react_flow_be/service/AttributeService.java - Enhanced
package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.ConnectionDto;
import com.example.react_flow_be.dto.AttributeDto;
import com.example.react_flow_be.entity.Attribute;
import com.example.react_flow_be.entity.Model;
import com.example.react_flow_be.repository.AttributeRepository;
import com.example.react_flow_be.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeService {
    
    private final AttributeRepository attributeRepository;
    private final ModelRepository modelRepository;
    private final ConnectionService connectionService;
    
    @Transactional
    public boolean updateAttribute(Long attributeId, String attributeName, String attributeType) {
        try {
            Optional<Attribute> attributeOpt = attributeRepository.findById(attributeId);
            if (attributeOpt.isPresent()) {
                Attribute attribute = attributeOpt.get();
                attribute.setName(attributeName);
                
                // Parse dataType and length from AttributeType
                if (attributeType.contains("(")) {
                    String dataType = attributeType.substring(0, attributeType.indexOf("("));
                    String lengthStr = attributeType.substring(attributeType.indexOf("(") + 1, attributeType.indexOf(")"));
                    attribute.setDataType(dataType);
                    try {
                        attribute.setLength(Integer.parseInt(lengthStr));
                    } catch (NumberFormatException e) {
                        if (lengthStr.contains(",")) {
                            String[] parts = lengthStr.split(",");
                            try {
                                attribute.setPrecisionValue(Integer.parseInt(parts[0].trim()));
                                attribute.setScaleValue(Integer.parseInt(parts[1].trim()));
                            } catch (NumberFormatException ex) {
                                // Ignore if can't parse precision/scale
                            }
                        }
                    }
                } else {
                    attribute.setDataType(attributeType);
                }
                
                boolean isForeignKey = attributeName.endsWith("_id") && !attribute.getIsPrimaryKey();
                attribute.setIsForeignKey(isForeignKey);
                
                attributeRepository.save(attribute);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error updating attribute {}: {}", attributeId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public boolean togglePrimaryKey(Long attributeId) {
        try {
            Optional<Attribute> attributeOpt = attributeRepository.findById(attributeId);
            if (attributeOpt.isPresent()) {
                Attribute attribute = attributeOpt.get();
                
                boolean newPKStatus = !attribute.getIsPrimaryKey();
                
                // If removing PK status and it has connections, remove them
                if (!newPKStatus && attribute.getIsPrimaryKey()) {
                    // This PK is being removed, remove any connections TO this attribute
                    connectionService.removeConnectionsToAttribute(attribute.getModel().getName(), attribute.getName());
                    log.info("Removed connections TO attribute {}.{} when removing PK status", 
                        attribute.getModel().getName(), attribute.getName());
                }
                
                attribute.setIsPrimaryKey(newPKStatus);
                
                // If setting as PK, remove FK status and add index
                if (newPKStatus) {
                    // If currently FK, remove its connection first
                    if (attribute.getIsForeignKey()) {
                        connectionService.removeConnectionsForAttribute(attributeId);
                        log.info("Removed FK connection when converting attribute {} to PK", attributeId);
                    }
                    
                    attribute.setIsForeignKey(false);
                    attribute.setIsNullable(false);
                    attribute.setHasIndex(true);
                    attribute.setIndexName("PRIMARY_" + attribute.getName());
                    attribute.setIndexType(Attribute.IndexType.PRIMARY);
                } else {
                    // If removing PK status, remove index (unless it's FK)
                    if (!attribute.getIsForeignKey()) {
                        attribute.setHasIndex(false);
                        attribute.setIndexName(null);
                        attribute.setIndexType(null);
                    }
                    attribute.setIsNullable(true);
                }
                
                attributeRepository.save(attribute);
                log.info("Toggled PK status for attribute {} to {}", attributeId, newPKStatus);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error toggling primary key for attribute {}: {}", attributeId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public boolean toggleForeignKey(Long attributeId) {
        try {
            Optional<Attribute> attributeOpt = attributeRepository.findById(attributeId);
            if (attributeOpt.isPresent()) {
                Attribute attribute = attributeOpt.get();
                
                boolean newFKStatus = !attribute.getIsForeignKey();
                
                // If removing FK status, remove its connection
                if (!newFKStatus && attribute.getIsForeignKey()) {
                    connectionService.removeForeignKeyConnection(attributeId);
                    log.info("Removed FK connection when converting attribute {} to normal", attributeId);
                }
                
                attribute.setIsForeignKey(newFKStatus);
                
                // If setting as FK, remove PK status and add index
                if (newFKStatus) {
                    // If currently PK, remove connections TO this attribute
                    if (attribute.getIsPrimaryKey()) {
                        connectionService.removeConnectionsToAttribute(attribute.getModel().getName(), attribute.getName());
                        log.info("Removed connections TO attribute {}.{} when converting PK to FK", 
                            attribute.getModel().getName(), attribute.getName());
                    }
                    
                    attribute.setIsPrimaryKey(false);
                    attribute.setHasIndex(true);
                    attribute.setIndexName("idx_" + attribute.getModel().getName().toLowerCase() + "_" + attribute.getName());
                    attribute.setIndexType(Attribute.IndexType.INDEX);
                } else {
                    // If removing FK status, remove index (unless it's PK)
                    if (!attribute.getIsPrimaryKey()) {
                        attribute.setHasIndex(false);
                        attribute.setIndexName(null);
                        attribute.setIndexType(null);
                    }
                }
                
                attributeRepository.save(attribute);
                log.info("Toggled FK status for attribute {} to {}", attributeId, newFKStatus);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error toggling foreign key for attribute {}: {}", attributeId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public Long addAttribute(Long modelId, String attributeName, String dataType) {
        try {
            Optional<Model> modelOpt = modelRepository.findById(modelId);
            if (modelOpt.isPresent()) {
                Model model = modelOpt.get();
                
                // Get next order number
                List<Attribute> existingAttributes = attributeRepository.findByModelIdOrderByAttributeOrder(model.getId());
                int nextOrder = existingAttributes.size();
                
                // Create new attribute
                Attribute newAttribute = new Attribute();
                newAttribute.setName(attributeName);
                newAttribute.setDataType(dataType);
                newAttribute.setModel(model);
                newAttribute.setAttributeOrder(nextOrder);
                newAttribute.setIsNullable(true);
                newAttribute.setIsPrimaryKey(false);
                newAttribute.setIsForeignKey(false);
                newAttribute.setIsUnique(false);
                newAttribute.setIsAutoIncrement(false);
                newAttribute.setHasIndex(false);
                
                // Set default length for VARCHAR
                if ("VARCHAR".equals(dataType)) {
                    newAttribute.setLength(255);
                }
                
                Attribute savedAttribute = attributeRepository.save(newAttribute);
                log.info("Added new attribute {} to model {}", attributeName, model.getName());
                return savedAttribute.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("Error adding attribute to model {}: {}", modelId, e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public boolean deleteAttribute(Long attributeId) {
        try {
            Optional<Attribute> attributeOpt = attributeRepository.findById(attributeId);
            if (attributeOpt.isPresent()) {
                Attribute attribute = attributeOpt.get();
                
                // Check if this is the only attribute
                List<Attribute> modelAttributes = attributeRepository.findByModelIdOrderByAttributeOrder(
                    attribute.getModel().getId()
                );
                
                // Don't allow deletion if it's the only attribute
                // if (modelAttributes.size() <= 1) {
                //     log.warn("Cannot delete the only attribute in model {}", attribute.getModel().getName());
                //     return false;
                // }
                
                // Remove any connections related to this attribute
                // if (attribute.getIsForeignKey()) {
                //     connectionService.removeConnectionsForAttribute(attributeId);
                //     log.info("Removed FK connections when deleting attribute {}", attributeId);
                // }
                
                if (attribute.getIsPrimaryKey()) {
                    connectionService.removeConnectionsToAttribute(attribute.getModel().getName(), attribute.getName());
                    log.info("Removed connections TO attribute {}.{} when deleting PK", 
                        attribute.getModel().getName(), attribute.getName());
                }
                
                attributeRepository.delete(attribute);
                
                // Reorder remaining attributes
                List<Attribute> remainingAttributes = attributeRepository.findByModelIdOrderByAttributeOrder(
                    attribute.getModel().getId()
                );
                for (int i = 0; i < remainingAttributes.size(); i++) {
                    Attribute attr = remainingAttributes.get(i);
                    attr.setAttributeOrder(i);
                    attributeRepository.save(attr);
                }
                
                log.info("Deleted attribute {} from model {}", attributeId, attribute.getModel().getName());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting attribute {}: {}", attributeId, e.getMessage(), e);
            return false;
        }
    }
    
    @Transactional
    public Attribute createAttribute(Model model, String name, String dataType, boolean isForeignKey, 
                           int order, boolean isNullable, boolean isPrimaryKey) {
        Attribute attribute = new Attribute();
        attribute.setDataType(dataType);
        attribute.setAttributeOrder(order);
        attribute.setIsNullable(isNullable);
        attribute.setIsPrimaryKey(isPrimaryKey);
        attribute.setIsForeignKey(isForeignKey);
        attribute.setModel(model);
        attribute.setName(name);
        
        if ("VARCHAR".equals(dataType)) {
            attribute.setLength(255);
        } else if ("BIGINT".equals(dataType)) {
            attribute.setLength(20);
        }
        
        if (isPrimaryKey) {
            attribute.setHasIndex(true);
            attribute.setIndexName("PRIMARY_" + name);
            attribute.setIndexType(Attribute.IndexType.PRIMARY);
        } else if (isForeignKey) {
            attribute.setHasIndex(true);
            attribute.setIndexName("idx_" + model.getName().toLowerCase() + "_" + name);
            attribute.setIndexType(Attribute.IndexType.INDEX);
        }
        
        return attributeRepository.save(attribute);
    }
    
    public AttributeDto convertToAttributeDto(Attribute attribute) {
        ConnectionDto connectionDto = null;
        if (!attribute.getConnection().isEmpty()) {
            connectionDto = new ConnectionDto(
                attribute.getConnection().get(0).getId(),
                attribute.getConnection().get(0).getTargetModel() != null ? attribute.getConnection().get(0).getTargetModel().getName() : "Unknown",
                attribute.getConnection().get(0).getTargetAttributeName(),
                attribute.getConnection().get(0).getForeignKeyName(),
                attribute.getConnection().get(0).getIsEnforced()
            );
        }
        
        return new AttributeDto(
            attribute.getId(),
            attribute.getName(),
            attribute.getDataType() + (attribute.getLength() != null ? "(" + attribute.getLength() + ")" : ""),
            attribute.getLength(),
            attribute.getPrecisionValue(),
            attribute.getScaleValue(),
            attribute.getIsNullable(),
            attribute.getIsPrimaryKey(),
            attribute.getIsForeignKey(),
            attribute.getIsUnique(),
            attribute.getIsAutoIncrement(),
            attribute.getDefaultValue(),
            attribute.getComment(),
            attribute.getAttributeOrder(),
            attribute.getHasIndex(),
            attribute.getIndexName(),
            attribute.getIndexType() != null ? attribute.getIndexType().name() : null,
            connectionDto
        );
    }
    
    public Attribute getAttributeByModelAndName(Model model, String attributeName) {
        return attributeRepository.findByModelIdAndName(model.getId(), attributeName)
            .orElse(null);
    }
    
    public List<AttributeDto> convertToDtoList(List<Attribute> attributes) {
        return attributes.stream()
            .map(this::convertToAttributeDto)
            .sorted((f1, f2) -> f1.getAttributeOrder().compareTo(f2.getAttributeOrder()))
            .collect(Collectors.toList());
    }
}