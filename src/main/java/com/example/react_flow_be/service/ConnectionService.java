package com.example.react_flow_be.service;

import com.example.react_flow_be.dto.ConnectionDto;
import com.example.react_flow_be.entity.*;
import com.example.react_flow_be.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    
    private final ConnectionRepository connectionRepository;
    
    @Transactional(readOnly = true)
    public List<Connection> getConnectionsByDiagram(Long diagramId) {
        // Lấy tất cả connections có field thuộc models của diagram này
        return connectionRepository.findAll().stream()
            .filter(conn -> conn.getField() != null && 
                          conn.getField().getModel().getDatabaseDiagram().getId().equals(diagramId))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Connection createConnection(Field sourceField, Model targetModel, String targetFieldName,
                                     Connection.ConnectionType connectionType, String fkName, 
                                     String color) {
        Connection connection = new Connection();
        
        connection.setConnectionType(connectionType);
        connection.setTargetFieldName(targetFieldName);
        connection.setForeignKeyName(fkName);
        connection.setStrokeColor(color);
        connection.setStrokeWidth(2);
        connection.setIsAnimated(true);
        connection.setTargetArrowType(Connection.ArrowType.CROW_FOOT);
        connection.setField(sourceField); // Set field relationship
        connection.setTargetModel(targetModel); // Set target model
        
        return connectionRepository.save(connection);
    }
    
    public ConnectionDto convertToConnectionDto(Connection connection) {
        return new ConnectionDto(
            connection.getId(),
            connection.getConnectionType().name(),
            connection.getTargetModel() != null ? connection.getTargetModel().getName() : "Unknown",
            connection.getTargetFieldName(),
            connection.getForeignKeyName(),
            connection.getOnUpdate() != null ? connection.getOnUpdate().name() : null,
            connection.getOnDelete() != null ? connection.getOnDelete().name() : null,
            connection.getIsEnforced(),
            connection.getStrokeColor(),
            connection.getStrokeWidth(),
            connection.getStrokeStyle(),
            connection.getIsAnimated(),
            connection.getSourceArrowType() != null ? connection.getSourceArrowType().name() : null,
            connection.getTargetArrowType() != null ? connection.getTargetArrowType().name() : null
        );
    }
}