package com.jjar.hephaestus.Controllers;

import com.jjar.hephaestus.Dto.YamlRequest;
import com.jjar.hephaestus.Dto.YamlResponse;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/yaml")
public class YAMLGeneratorController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeneratedFileService generatedFileService;

    @Autowired
    private YAMLGeneratorService yamlGeneratorService;

    @PostMapping()
    public ResponseEntity<?> generateYAML(@RequestBody YamlRequest yamlRequest,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            YamlResponse yamlResponse = yamlGeneratorService.generateAndUploadYAML(yamlRequest, token, true);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                    yamlRequest.getWorkflowName().replaceAll("\\s+", "_") + ".yaml");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            Map<String, Object> response = new HashMap<>();
            response.put("yamlFile", Base64.getEncoder().encodeToString(yamlResponse.getYamlFile())); // Encode file in Base64
            response.put("documentation", yamlResponse.getDocumentation());

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
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
