package com.example.azureuploaddemo.controller;

//DTO class for JSON request body
public class UploadRequest {
    private String filePath;
    private String sasUrl;
    private String uniqueId;  

    public String getFilePath() { return filePath; }
    public String getSasUrl() { return sasUrl; }
    public String getUniqueId() { return uniqueId; }

    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setSasUrl(String sasUrl) { this.sasUrl = sasUrl; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
}
