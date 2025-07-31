package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "connections")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Connection extends Link {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionType connectionType;
    
    // Foreign key information
    @Column(length = 100)
    private String foreignKeyName;
    
    @Column(length = 100)
    private String sourceFieldName; // Field in source model
    
    @Column(length = 100)
    private String targetFieldName; // Field in target model
    
    // Constraint options
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CascadeAction onUpdate = CascadeAction.NO_ACTION;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CascadeAction onDelete = CascadeAction.NO_ACTION;
    
    @Column(nullable = false)
    private Boolean isEnforced = true; // Whether FK constraint is enforced
    
    // Relationships to Models (specialized from Connection's generic node references)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_model_id", nullable = false)
    @JsonBackReference("source-connections")
    private Model sourceModel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_model_id", nullable = false)
    @JsonBackReference("target-connections")
    private Model targetModel;
    
    public enum ConnectionType {
        ONE_TO_ONE,
        ONE_TO_MANY,
        MANY_TO_ONE,
        MANY_TO_MANY,
        INHERITANCE,     // For class diagrams
        COMPOSITION,     // For class diagrams
        AGGREGATION,     // For class diagrams
        DEPENDENCY       // For class diagrams
    }
    
    public enum CascadeAction {
        NO_ACTION,
        RESTRICT,
        CASCADE,
        SET_NULL,
        SET_DEFAULT
    }
}