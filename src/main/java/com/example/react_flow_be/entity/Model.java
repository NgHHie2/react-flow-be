package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private Double positionX = 0.0;
    private Double positionY = 0.0;
    private Boolean isChild = false;
    private String backgroundColor = "#3d5787";
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Field> fields;
    
    @OneToMany(mappedBy = "sourceModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("source-connections")
    private List<Connection> sourceConnections;
    
    @OneToMany(mappedBy = "targetModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("target-connections")
    private List<Connection> targetConnections;
}
