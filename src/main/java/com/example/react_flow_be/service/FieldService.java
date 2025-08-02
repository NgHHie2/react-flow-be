package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.ConnectionDto;
import com.example.react_flow_be.dto.FieldDto;
import com.example.react_flow_be.entity.Field;
import com.example.react_flow_be.entity.Model;
import com.example.react_flow_be.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldService {
    
    private final FieldRepository fieldRepository;
    
    @Transactional
    public boolean updateField(Long fieldId, String fieldName, String fieldType) {
        try {
            Optional<Field> fieldOpt = fieldRepository.findById(fieldId);
            if (fieldOpt.isPresent()) {
                Field field = fieldOpt.get();
                field.setName(fieldName);
                
                // Parse dataType and length from fieldType
                if (fieldType.contains("(")) {
                    String dataType = fieldType.substring(0, fieldType.indexOf("("));
                    String lengthStr = fieldType.substring(fieldType.indexOf("(") + 1, fieldType.indexOf(")"));
                    field.setDataType(dataType);
                    try {
                        field.setLength(Integer.parseInt(lengthStr));
                    } catch (NumberFormatException e) {
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
                
                boolean isForeignKey = fieldName.endsWith("_id") && !field.getIsPrimaryKey();
                field.setIsForeignKey(isForeignKey);
                
                fieldRepository.save(field);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Transactional
    public Field createField(Model model, String name, String dataType, boolean isForeignKey, 
                           int order, boolean isNullable, boolean isPrimaryKey) {
        Field field = new Field();
        field.setName(name);
        field.setDataType(dataType);
        field.setFieldOrder(order);
        field.setIsNullable(isNullable);
        field.setIsPrimaryKey(isPrimaryKey);
        field.setIsForeignKey(isForeignKey);
        field.setModel(model);
        
        if ("VARCHAR".equals(dataType)) {
            field.setLength(255);
        } else if ("BIGINT".equals(dataType)) {
            field.setLength(20);
        }
        
        if (isPrimaryKey) {
            field.setHasIndex(true);
            field.setIndexName("PRIMARY");
            field.setIndexType(Field.IndexType.PRIMARY);
        } else if (isForeignKey) {
            field.setHasIndex(true);
            field.setIndexName("idx_" + model.getName().toLowerCase() + "_" + name);
            field.setIndexType(Field.IndexType.INDEX);
        }
        
        return fieldRepository.save(field);
    }
    
    public FieldDto convertToFieldDto(Field field) {
        ConnectionDto connectionDto = null;
        if (field.getConnection() != null) {
            connectionDto = new ConnectionDto(
                field.getConnection().getId(),
                field.getConnection().getConnectionType().name(),
                field.getConnection().getTargetModel() != null ? field.getConnection().getTargetModel().getName() : "Unknown",
                field.getConnection().getTargetFieldName(),
                field.getConnection().getForeignKeyName(),
                field.getConnection().getOnUpdate() != null ? field.getConnection().getOnUpdate().name() : null,
                field.getConnection().getOnDelete() != null ? field.getConnection().getOnDelete().name() : null,
                field.getConnection().getIsEnforced(),
                field.getConnection().getStrokeColor(),
                field.getConnection().getStrokeWidth(),
                field.getConnection().getStrokeStyle(),
                field.getConnection().getIsAnimated(),
                field.getConnection().getSourceArrowType() != null ? field.getConnection().getSourceArrowType().name() : null,
                field.getConnection().getTargetArrowType() != null ? field.getConnection().getTargetArrowType().name() : null
            );
        }
        
        return new FieldDto(
            field.getId(),
            field.getName(),
            field.getDataType() + (field.getLength() != null ? "(" + field.getLength() + ")" : ""),
            field.getLength(),
            field.getPrecision(),
            field.getScale(),
            field.getIsNullable(),
            field.getIsPrimaryKey(),
            field.getIsForeignKey(),
            field.getIsUnique(),
            field.getIsAutoIncrement(),
            field.getDefaultValue(),
            field.getComment(),
            field.getFieldOrder(),
            field.getHasIndex(),
            field.getIndexName(),
            field.getIndexType() != null ? field.getIndexType().name() : null,
            connectionDto // Set connection information từ field.getConnection()
        );
    }
    
    public Field getFieldByModelAndName(Model model, String fieldName) {
        return model.getFields().stream()
            .filter(field -> fieldName.equals(field.getName()))
            .findFirst()
            .orElse(null);
    }
    
    public List<FieldDto> convertToDtoList(List<Field> fields) {
        return fields.stream()
            .map(this::convertToFieldDto)
            .sorted((f1, f2) -> f1.getFieldOrder().compareTo(f2.getFieldOrder()))
            .collect(Collectors.toList());
    }
}
