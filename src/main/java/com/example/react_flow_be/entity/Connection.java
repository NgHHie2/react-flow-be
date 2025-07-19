package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // Tên field tạo connection
    
    private String connectionType = "ONE_TO_ONE"; // ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
    private Boolean isAnimated = true;
    private String edgeColor = "#b1b1b7";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_model_id", nullable = false)
    @JsonBackReference("source-connections")
    private Model sourceModel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_model_id", nullable = false)
    @JsonBackReference("target-connections")
    private Model targetModel;
}
