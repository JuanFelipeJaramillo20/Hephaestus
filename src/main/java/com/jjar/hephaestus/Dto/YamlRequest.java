package com.jjar.hephaestus.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class YamlRequest {
    // Getters and setters
    private String workflowName;
    private List<String> triggerEvents; // Multiple trigger events (e.g., push, pull_request)
    private List<String> branches; // Multiple branches for triggers
    private String buildTool; // Build tool (e.g., Maven, Gradle)
    private String buildProjectPath; // Build project path
    private String testBuildTool; // Build tool for testing
    private String testProjectPath; // Test project path
    private String cloudProvider; // Cloud provider (e.g., AWS, Azure, Google Cloud)
    private String deploymentType; // Deployment type (e.g., Lambda/Function)

}
