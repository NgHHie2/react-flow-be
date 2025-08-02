package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String version;
    private MigrationType type;
    private String sqlScript;
    private String rollbackScript;
    private MigrationStatus status;
    private String executionLog;
    private Long executionTimeMs;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    
    @ManyToOne
    @JoinColumn(name = "database_diagram_id")
    private DatabaseDiagram databaseDiagram;
    
    public enum MigrationType {
        CREATE_TABLE, ALTER_TABLE, DROP_TABLE, CREATE_INDEX, DROP_INDEX,
        INSERT_DATA, UPDATE_DATA, DELETE_DATA, CUSTOM
    }
    
    public enum MigrationStatus {
        PENDING, EXECUTING, SUCCESS, FAILED, ROLLED_BACK
    }
}
