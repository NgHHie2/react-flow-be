package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;
    
    @Column(nullable = false, length = 200)
    private String description; // Human-readable description of the action
    
    @Column(length = 100)
    private String targetId; // ID of the affected element (shape, text, connection)
    
    @Enumerated(EnumType.STRING)
    @Column
    private TargetType targetType; // Type of the affected element
    
    @Lob
    @Column
    private String oldValue; // JSON representation of old state
    
    @Lob
    @Column
    private String newValue; // JSON representation of new state
    
    @Column(length = 100)
    private String userId; // User who performed the action (could be session ID)
    
    @Column(length = 100)
    private String sessionId; // Session ID for tracking
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagram_id", nullable = false)
    @JsonBackReference("diagram-history")
    private Diagram diagram;
    
    public enum ActionType {
        // Shape actions
        SHAPE_CREATED,
        SHAPE_UPDATED,
        SHAPE_DELETED,
        SHAPE_MOVED,
        SHAPE_RESIZED,
        SHAPE_STYLED,
        
        // Text actions
        TEXT_CREATED,
        TEXT_UPDATED,
        TEXT_DELETED,
        TEXT_MOVED,
        
        // Connection actions
        CONNECTION_CREATED,
        CONNECTION_UPDATED,
        CONNECTION_DELETED,
        
        // Model actions (for ER diagrams)
        MODEL_CREATED,
        MODEL_UPDATED,
        MODEL_DELETED,
        FIELD_CREATED,
        FIELD_UPDATED,
        FIELD_DELETED,
        
        // Diagram actions
        DIAGRAM_CREATED,
        DIAGRAM_UPDATED,
        DIAGRAM_RENAMED,
        
        // Collaboration actions
        USER_JOINED,
        USER_LEFT,
        PERMISSION_CHANGED,
        
        // Bulk actions
        BULK_UPDATE,
        IMPORT_DATA,
        EXPORT_DATA
    }
    
    public enum TargetType {
        SHAPE,
        TEXT,
        CONNECTION,
        MODEL,
        FIELD,
        DIAGRAM,
        USER
    }
}