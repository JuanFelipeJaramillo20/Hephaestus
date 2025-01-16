package com.jjar.hephaestus.Service;

import com.jjar.hephaestus.Dto.YamlRequest;
import com.jjar.hephaestus.Dto.YamlResponse;
import com.jjar.hephaestus.Entity.GeneratedFile;
import com.jjar.hephaestus.Entity.User;
import com.jjar.hephaestus.Utils.JWTUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private String resolvedOpenaiApiKey;

    private String resolvedBucketName;

    @PostConstruct
    private void initializeBucketName() {
        System.out.println("Initializing bucket name: " + bucketName);
        System.out.println("Initializing openai key: " + openaiApiKey);
        this.resolvedBucketName = bucketName;
        this.resolvedOpenaiApiKey = openaiApiKey;
    }

    public YamlResponse generateAndUploadYAML(YamlRequest yamlRequest, String token, boolean useGPT) throws IOException, TemplateException {
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
        String documentation = null;

        if (useGPT) {
            documentation = generateDocumentationWithGPT(yamlContent, yamlRequest.getLanguage());
            //System.out.println("Documentation: " + documentation);
        }

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

        return new YamlResponse(yamlContent.getBytes(StandardCharsets.UTF_8), documentation);
    }


    private String validateYAMLWithGPT(String yamlContent) {
        String prompt = "Please validate this YAML configuration and highlight any potential issues:\n\n" + yamlContent;
        return callGPTApi(prompt);
    }

    private String generateDocumentationWithGPT(String yamlContent, String language) {
        String prompt;

        if ("es".equalsIgnoreCase(language)) {
            prompt = "Genera una documentación detallada y formal para la siguiente configuración YAML. La documentación debe incluir una explicación paso a paso de cada sección y evitar un lenguaje conversacional o comentarios:\n\n" + yamlContent;
        } else {
            prompt = "Generate a formal and detailed documentation for the following YAML configuration. The documentation should include a step-by-step explanation of each section, avoiding conversational language or commentary:\n\n" + yamlContent;
        }

        return callGPTApi(prompt);
    }


    private String callGPTApi(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        String openaiApiUrl = "https://api.openai.com/v1/chat/completions"; // Correct endpoint
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a YAML configuration expert."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resolvedOpenaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(openaiApiUrl, request, Map.class);

            // Parse the response to get the generated content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
            }
            throw new RuntimeException("No response from GPT API.");
        } catch (Exception e) {
            throw new RuntimeException("Error calling GPT API: " + e.getMessage(), e);
        }
    }


}
