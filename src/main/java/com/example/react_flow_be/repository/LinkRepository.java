package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Link;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByDiagramId(Long diagramId);
    Optional<Link> findByEdgeId(String edgeId);
    List<Link> findBySourceNodeId(String sourceNodeId);
    List<Link> findByTargetNodeId(String targetNodeId);
    
    @Query("SELECT l FROM Link l WHERE l.sourceNodeId = :nodeId OR l.targetNodeId = :nodeId")
    List<Link> findByNodeId(String nodeId);
}
