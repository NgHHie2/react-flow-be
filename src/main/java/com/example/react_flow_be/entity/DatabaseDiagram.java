package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseDiagram extends Diagram {
    private DatabaseType databaseType;
    private String version;
    private String charset;
    private String collation;
    
    @OneToMany(mappedBy = "databaseDiagram")
    private List<Migration> migrations;
    
    @OneToMany(mappedBy = "databaseDiagram")
    private List<Model> models; // DatabaseDiagram có models trực tiếp
    
    public enum DatabaseType {
        MYSQL, POSTGRESQL, ORACLE, SQL_SERVER, SQLITE, MONGODB, REDIS, CASSANDRA, CUSTOM
    }
}
