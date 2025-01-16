package com.jjar.hephaestus.Dto;

public class YamlResponse {
    private byte[] yamlFile;
    private String documentation;

    public YamlResponse(byte[] yamlFile, String documentation) {
        this.yamlFile = yamlFile;
        this.documentation = documentation;
    }

    public byte[] getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(byte[] yamlFile) {
        this.yamlFile = yamlFile;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
