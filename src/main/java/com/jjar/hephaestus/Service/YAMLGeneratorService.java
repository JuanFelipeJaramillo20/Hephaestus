package com.jjar.hephaestus.Service;

import com.jjar.hephaestus.Dto.YamlRequest;
import com.jjar.hephaestus.Entity.GeneratedFile;
import com.jjar.hephaestus.Entity.User;
import com.jjar.hephaestus.Utils.JWTUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class YAMLGeneratorService {

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private GeneratedFileService generatedFileService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private String resolvedBucketName;

    @PostConstruct
    private void initializeBucketName() {
        System.out.println("Initializing bucket name: " + bucketName);
        this.resolvedBucketName = bucketName;
    }

    public byte[] generateAndUploadYAML(YamlRequest yamlRequest, String token) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate("workflow-template.yaml.ftl");

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("workflowName", yamlRequest.getWorkflowName());
        templateData.put("triggerEvents", yamlRequest.getTriggerEvents());
        templateData.put("branches", yamlRequest.getBranches());
        templateData.put("buildTool", yamlRequest.getBuildTool());
        templateData.put("buildProjectPath", yamlRequest.getBuildProjectPath());
        templateData.put("testBuildTool", yamlRequest.getTestBuildTool());
        templateData.put("testProjectPath", yamlRequest.getTestProjectPath());
        templateData.put("cloudProvider", yamlRequest.getCloudProvider());
        templateData.put("deploymentType", yamlRequest.getDeploymentType());

        StringWriter writer = new StringWriter();
        template.process(templateData, writer);

        String yamlContent = writer.toString();

        if (token != null && !token.isBlank()) {
            String userEmail = JWTUtil.extractEmail(token.substring(7));

            User user = userService.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String key = user.getId() + "/" + yamlRequest.getWorkflowName().replaceAll("\\s+", "_") + ".yaml";
            s3Service.uploadFile(key, yamlContent);

            GeneratedFile fileRecord = new GeneratedFile();
            fileRecord.setUser(user);
            fileRecord.setFileName(key);
            fileRecord.setGeneratedAt(new Date());
            fileRecord.setFileUrl("https://" + resolvedBucketName + ".s3.amazonaws.com/" + key);
            generatedFileService.save(fileRecord);
        }

        return yamlContent.getBytes(StandardCharsets.UTF_8);
    }

}
