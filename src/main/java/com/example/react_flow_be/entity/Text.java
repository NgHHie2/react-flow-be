package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "texts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Text {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nodeId; // Unique identifier for frontend
    
    @Lob
    @Column(nullable = false)
    private String content;
    
    // Position
    @Column(nullable = false)
    private Double positionX = 0.0;
    
    @Column(nullable = false)
    private Double positionY = 0.0;
    
    // Size
    @Column
    private Double width;
    
    @Column
    private Double height;
    
    // Text styling
    @Column(nullable = false, length = 50)
    private String fontFamily = "Arial";
    
    @Column(nullable = false)
    private Integer fontSize = 14;
    
    @Column(nullable = false)
    private String fontColor = "#000000";
    
    @Column(nullable = false)
    private Boolean isBold = false;
    
    @Column(nullable = false)
    private Boolean isItalic = false;
    
    @Column(nullable = false)
    private Boolean isUnderline = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextAlign textAlign = TextAlign.LEFT;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerticalAlign verticalAlign = VerticalAlign.TOP;
    
    // Background
    @Column
    private String backgroundColor;
    
    @Column
    private Integer padding = 0;
    
    // Z-index
    @Column
    private Integer zIndex = 0;
    
    // Rotation
    @Column
    private Double rotation = 0.0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagram_id", nullable = false)
    @JsonBackReference("diagram-texts")
    private Diagram diagram;
    
    public enum TextAlign {
        LEFT, CENTER, RIGHT, JUSTIFY
    }
    
    public enum VerticalAlign {
        TOP, MIDDLE, BOTTOM
    }
}
