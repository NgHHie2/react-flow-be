package com.example.react_flow_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Connection;

@Repository  
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findByAttributeId(Long attributeId);
    List<Connection> findByTargetModelId(Long targetModelId);
    
    @Query("SELECT c FROM Connection c JOIN c.targetModel tm WHERE tm.name = :targetModelName AND c.targetAttributeName = :targetAttributeName")
    List<Connection> findByTargetModelNameAndTargetAttributeName(
        @Param("targetModelName") String targetModelName, 
        @Param("targetAttributeName") String targetAttributeName
    );
}