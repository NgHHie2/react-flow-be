package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    private DiagramType type;
    private Boolean isPublic;
    private Boolean isTemplate;
    
    // Canvas settings
    private String canvasData;
    private Double zoomLevel;
    private Double panX;
    private Double panY;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;
    
    public enum DiagramType {
        ER_DIAGRAM, FLOWCHART, UML_CLASS, NETWORK, ORGANIZATIONAL, MIND_MAP, WIREFRAME, CUSTOM
    }
}