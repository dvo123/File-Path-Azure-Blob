package com.example.azureuploaddemo.controller;

import com.example.azureuploaddemo.service.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final AzureBlobService azureBlobService;

    @Autowired
    public FileUploadController(AzureBlobService azureBlobService) {
        this.azureBlobService = azureBlobService;
    }

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<ApiResponse>> uploadFile(@RequestBody UploadRequest request) {
        logger.info("Received file upload request: {}, Unique ID: {}", request.getFilePath(), request.getUniqueId());

        // Validate File Path (Ensure file exists)
        File file = new File(request.getFilePath());
        if (!file.exists() || file.isDirectory()) {
            String errorMessage = "Invalid file path: " + request.getFilePath();
            logger.error(errorMessage);
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse(false, null, errorMessage))
            );
        }

        // Validate SAS URL Format
        if (!isValidSasUrl(request.getSasUrl())) {
            String errorMessage = "Invalid SAS URL format: " + request.getSasUrl();
            logger.error(errorMessage);
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse(false, null, errorMessage))
            );
        }

        // Proceed with file upload
        return azureBlobService.uploadFile(request.getFilePath(), request.getSasUrl(), request.getUniqueId())
                .thenApply(result -> {
                    logger.info("Upload result: {}", result);
                    return ResponseEntity.ok(new ApiResponse(true, result, null));
                })
                .exceptionally(throwable -> {
                    String errorMessage = throwable.getCause().getMessage();
                    logger.error("File upload failed: {} - Error: {}", request.getFilePath(), errorMessage);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse(false, null, errorMessage));
                });
    }

    // Helper Method: Check if SAS URL is valid.
    private boolean isValidSasUrl(String sasUrl) {
        if (sasUrl == null || !sasUrl.contains("?") || !sasUrl.contains("sv=")) {
            return false;
        }
        return true;
    }
}
