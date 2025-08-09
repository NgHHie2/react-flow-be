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
        // Lấy tất cả connections có Attribute thuộc models của diagram này
        return connectionRepository.findAll().stream()
            .filter(conn -> conn.getAttribute() != null && 
                          conn.getAttribute().getModel().getDatabaseDiagram().getId().equals(diagramId))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Connection createConnection(Attribute sourceAttribute, Model targetModel, String targetAttributeName,
                                     String fkName, 
                                     String color) {
        Connection connection = new Connection();
        
        connection.setTargetAttributeName(targetAttributeName);
        connection.setForeignKeyName(fkName);
        connection.setAttribute(sourceAttribute); // Set Attribute relationship
        connection.setTargetModel(targetModel); // Set target model
        
        return connectionRepository.save(connection);
    }
    
    public ConnectionDto convertToConnectionDto(Connection connection) {
        return new ConnectionDto(
            connection.getId(),
            connection.getTargetModel() != null ? connection.getTargetModel().getName() : "Unknown",
            connection.getTargetAttributeName(),
            connection.getForeignKeyName(),
            connection.getIsEnforced()
        );
    }
}