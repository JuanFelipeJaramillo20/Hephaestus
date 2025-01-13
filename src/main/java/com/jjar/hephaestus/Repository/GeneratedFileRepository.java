package com.jjar.hephaestus.Repository;

import com.jjar.hephaestus.Entity.GeneratedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedFileRepository extends JpaRepository<GeneratedFile, Long> {
    List<GeneratedFile> findByUserId(Long userId);
}
