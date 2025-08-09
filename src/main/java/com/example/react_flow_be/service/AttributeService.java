package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.ConnectionDto;
import com.example.react_flow_be.dto.AttributeDto;
import com.example.react_flow_be.entity.Attribute;
import com.example.react_flow_be.entity.Model;
import com.example.react_flow_be.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributeService {
    
    private final AttributeRepository attributeRepository;
    
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
            attribute.setIndexName("PRIMARY");
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
        if (attribute.getConnection() != null) {
            connectionDto = new ConnectionDto(
                attribute.getConnection().getId(),
                attribute.getConnection().getTargetModel() != null ? attribute.getConnection().getTargetModel().getName() : "Unknown",
                attribute.getConnection().getTargetAttributeName(),
                attribute.getConnection().getForeignKeyName(),
                attribute.getConnection().getIsEnforced()
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
            connectionDto // Set connection information từ Attribute.getConnection()
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
