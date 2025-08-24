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
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String dataType;
    private Integer length;
    private Integer precisionValue;
    private Integer scaleValue;
    
    private Boolean isNullable;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;
    private Boolean isUnique;
    private Boolean isAutoIncrement;
    
    private String defaultValue;
    private String comment;
    private Integer attributeOrder;
    
    private Boolean hasIndex;
    private String indexName;
    private IndexType indexType;
    
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;
    
    @OneToOne(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private Connection connection;
    
    public enum IndexType {
        PRIMARY, UNIQUE, INDEX, FULLTEXT, SPATIAL
    }
}
