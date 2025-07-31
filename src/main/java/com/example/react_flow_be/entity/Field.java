package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String dataType; // VARCHAR, INT, BIGINT, etc.
    
    @Column
    private Integer length; // Length for VARCHAR, CHAR, etc.
    
    @Column(name = "decimal_precision") // Đổi tên column để tránh reserved keyword
    private Integer precision; // For DECIMAL, NUMERIC
    
    @Column(name = "decimal_scale") // Đổi tên column để rõ ràng hơn
    private Integer scale; // For DECIMAL, NUMERIC
    
    @Column(nullable = false)
    private Boolean isNullable = true;
    
    @Column(nullable = false)
    private Boolean isPrimaryKey = false;
    
    @Column(nullable = false)
    private Boolean isForeignKey = false;
    
    @Column(nullable = false)
    private Boolean isUnique = false;
    
    @Column(nullable = false)
    private Boolean isAutoIncrement = false;
    
    @Column
    private String defaultValue;
    
    @Column(length = 500)
    private String comment;
    
    @Column(nullable = false)
    private Integer fieldOrder = 0;
    
    // Index information
    @Column(nullable = false)
    private Boolean hasIndex = false;
    
    @Column
    private String indexName;
    
    @Enumerated(EnumType.STRING)
    @Column
    private IndexType indexType;
    
    // Connection information
    @Column(nullable = false)
    private Boolean hasConnections = false;
    
    // Connections
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    @JsonBackReference("model-fields")
    private Model model;
    
    public enum IndexType {
        PRIMARY,
        UNIQUE,
        INDEX,
        FULLTEXT,
        SPATIAL
    }
}