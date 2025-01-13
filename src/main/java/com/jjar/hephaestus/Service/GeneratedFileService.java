package com.jjar.hephaestus.Service;

import com.jjar.hephaestus.Entity.GeneratedFile;
import com.jjar.hephaestus.Repository.GeneratedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratedFileService {

    @Autowired
    private GeneratedFileRepository generatedFileRepository;

    public List<GeneratedFile> findByUserId(Long userId) {
        return generatedFileRepository.findByUserId(userId);
    }

    public void save(GeneratedFile generatedFile) {
        generatedFileRepository.save(generatedFile);
    }
}
