package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "migrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Migration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private String version;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationType type;
    
    @Lob
    @Column(nullable = false)
    private String sqlScript; // SQL migration script
    
    @Lob
    @Column
    private String rollbackScript; // Rollback SQL script
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationStatus status = MigrationStatus.PENDING;
    
    @Column
    private String executionLog; // Log of execution results
    
    @Column
    private Long executionTimeMs; // Execution time in milliseconds
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime executedAt;
    
    // Relationships - Reference tới database_diagrams table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false)
    @JsonBackReference("database-migrations")
    private Database database;
    
    public enum MigrationType {
        CREATE_TABLE,
        ALTER_TABLE,
        DROP_TABLE,
        CREATE_INDEX,
        DROP_INDEX,
        INSERT_DATA,
        UPDATE_DATA,
        DELETE_DATA,
        CUSTOM
    }
    
    public enum MigrationStatus {
        PENDING,
        EXECUTING,
        SUCCESS,
        FAILED,
        ROLLED_BACK
    }
}