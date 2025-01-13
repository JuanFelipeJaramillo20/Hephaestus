package com.jjar.hephaestus.Controllers;

import com.jjar.hephaestus.Dto.YamlRequest;
import com.jjar.hephaestus.Entity.GeneratedFile;
import com.jjar.hephaestus.Entity.User;
import com.jjar.hephaestus.Service.GeneratedFileService;
import com.jjar.hephaestus.Service.S3Service;
import com.jjar.hephaestus.Service.UserService;
import com.jjar.hephaestus.Service.YAMLGeneratorService;
import com.jjar.hephaestus.Utils.JWTUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/yaml")
public class YAMLGeneratorController {

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private GeneratedFileService generatedFileService;

    @Autowired
    private YAMLGeneratorService yamlGeneratorService;

    @PostMapping()
    public ResponseEntity<byte[]> generateYAML(@RequestBody YamlRequest yamlRequest,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            byte[] yamlBytes = yamlGeneratorService.generateAndUploadYAML(yamlRequest, token);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                    yamlRequest.getWorkflowName().replaceAll("\\s+", "_") + ".yaml");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/yaml");

            return new ResponseEntity<>(yamlBytes, headers, HttpStatus.OK);
        } catch (IOException | TemplateException e) {
            return new ResponseEntity<>(e.getMessage().getBytes(StandardCharsets.UTF_8), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/history")
    public ResponseEntity<List<GeneratedFile>> getFileHistory(@RequestHeader("Authorization") String token) {
        String userEmail = JWTUtil.extractEmail(token.substring(7));
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GeneratedFile> files = generatedFileService.findByUserId(user.getId());
        return ResponseEntity.ok(files);
    }
}
