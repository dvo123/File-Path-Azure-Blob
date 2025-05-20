package com.example.azureuploaddemo.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Service
public class AzureBlobService {

    private static final Logger logger = LoggerFactory.getLogger(AzureBlobService.class);

    public CompletableFuture<String> uploadFile(String filePath, String sasUrl, String uniqueId) {
        logger.info("Starting file upload. FilePath: {}, Unique ID: {}", filePath, uniqueId);

        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("File does not exist: {}", filePath);
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        if (!isValidSasUrl(sasUrl)) {
            logger.error("Invalid SAS URL: {}", sasUrl);
            throw new IllegalArgumentException("Invalid SAS URL format. Please check the URL and try again.");
        }

        String endpoint = sasUrl.substring(0, sasUrl.indexOf("?"));
        String sasToken = sasUrl.substring(sasUrl.indexOf("?") + 1);

        // Generate a blob name using `uniqueId`
        String fileName = file.getName();
        String blobName = uniqueId + "_" + fileName;

        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(endpoint)
                .sasToken(sasToken)
                .containerName("ntg")
                .blobName(blobName)
                .buildClient();

        // Check if file already exists in Azure Blob
        if (blobExists(blobClient)) {
            logger.info("File already exists in Azure Blob: {}", blobName);
            return CompletableFuture.completedFuture("OK: Object already exists");
        }

        try {
            logger.info("Uploading file: {}", blobName);
            blobClient.uploadFromFile(filePath, true);
            logger.info("File uploaded successfully: {}", blobName);
            return CompletableFuture.completedFuture("File uploaded successfully: " + blobName);
        } catch (Exception e) {
            logger.error("Upload failed for file: {}", blobName, e);
            throw new RuntimeException("Upload failed: " + e.getMessage(), e);
        }
    }

    // Check if Blob already exists
    private boolean blobExists(BlobClient blobClient) {
        try {
            BlobProperties properties = blobClient.getProperties();
            return properties != null;
        } catch (Exception e) {
            return false; // Blob does not exist
        }
    }

    // Validate SAS URL format
    private boolean isValidSasUrl(String sasUrl) {
        try {
            URL url = new URL(sasUrl);
            return url.getQuery() != null && url.getQuery().contains("sig=");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}