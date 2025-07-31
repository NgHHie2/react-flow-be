package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Text;

import java.util.List;
import java.util.Optional;

@Repository
public interface TextRepository extends JpaRepository<Text, Long> {
    List<Text> findByDiagramId(Long diagramId);
    Optional<Text> findByNodeId(String nodeId);
}
