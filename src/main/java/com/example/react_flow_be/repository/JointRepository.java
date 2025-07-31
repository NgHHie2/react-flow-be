package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Joint;

import java.util.List;

@Repository
public interface JointRepository extends JpaRepository<Joint, Long> {
    List<Joint> findByLinkIdOrderByOrderIndex(Long linkId);
}