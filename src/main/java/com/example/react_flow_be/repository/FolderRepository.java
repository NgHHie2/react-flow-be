package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Folder;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwnerIdAndParentFolderIsNull(Long ownerId);
    List<Folder> findByParentFolderId(Long parentFolderId);
    
    @Query("SELECT f FROM Folder f WHERE f.owner.id = :ownerId AND f.name LIKE %:name%")
    List<Folder> findByOwnerIdAndNameContaining(@Param("ownerId") Long ownerId, @Param("name") String name);
}