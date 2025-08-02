package com.example.react_flow_be.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String edgeId;
    private String label;
    private ConnectionType connectionType;
    private String foreignKeyName;
    private CascadeAction onUpdate;
    private CascadeAction onDelete;
    private Boolean isEnforced;
    
    // Target information
    private String targetFieldName;
    
    // Style cho visualization
    private LinkType linkType;
    private ArrowType sourceArrowType;
    private ArrowType targetArrowType;
    private String strokeColor;
    private Integer strokeWidth;
    private String strokeStyle;
    private Boolean isAnimated;
    private String pathData;
    private Integer zIndex;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "field_id")
    private Field field;
    
    @ManyToOne
    @JoinColumn(name = "target_model_id")
    private Model targetModel;
    
    public enum ConnectionType {
        ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY,
        INHERITANCE, COMPOSITION, AGGREGATION, DEPENDENCY
    }
    
    public enum CascadeAction {
        NO_ACTION, RESTRICT, CASCADE, SET_NULL, SET_DEFAULT
    }
    
    public enum LinkType {
        STRAIGHT, BEZIER, STEP, SMOOTH_STEP, CUSTOM
    }
    
    public enum ArrowType {
        NONE, ARROW, DIAMOND, CIRCLE, SQUARE, TRIANGLE, CROW_FOOT
    }
}