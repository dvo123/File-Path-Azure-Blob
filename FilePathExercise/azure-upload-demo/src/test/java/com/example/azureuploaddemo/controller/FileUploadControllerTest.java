package com.example.azureuploaddemo.controller;

import com.example.azureuploaddemo.service.AzureBlobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @InjectMocks
    private FileUploadController fileUploadController;

    @Mock
    private AzureBlobService azureBlobService;

    @Test
    void testUploadFile_Success() {
        UploadRequest request = new UploadRequest();
        request.setFilePath("C:/Users/David Vo/Desktop/FilePathExercise/azure-upload-demo/testfile.txt");
        request.setSasUrl("https://fsgsealrsgnrsta.blob.core.windows.net/ntg?sv=2023-01-03&st=2025-03-11T03%3A09%3A36Z&se=2025-03-15T03%3A09%3A00Z&sr=c&sp=racwl&sig=DDeP19gRstAEjHao8YC%2FUOdrxyX%2BHo%2B0rdaLNYGeRNg%3D");
        request.setUniqueId("123456");

        when(azureBlobService.uploadFile(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture("File uploaded successfully: test-unique-id_testfile.txt"));

        CompletableFuture<ResponseEntity<ApiResponse>> response = fileUploadController.uploadFile(request);

        assertEquals(OK, response.join().getStatusCode());
        assertEquals("File uploaded successfully: test-unique-id_testfile.txt", response.join().getBody().getMessage());
    }

    @Test
    void testUploadFile_AlreadyExists() {
        UploadRequest request = new UploadRequest();
        request.setFilePath("C:/Users/David Vo/Desktop/FilePathExercise/azure-upload-demo/testfile.txt");
        request.setSasUrl("https://fsgsealrsgnrsta.blob.core.windows.net/ntg?sv=2023-01-03&st=2025-03-11T03%3A09%3A36Z&se=2025-03-15T03%3A09%3A00Z&sr=c&sp=racwl&sig=DDeP19gRstAEjHao8YC%2FUOdrxyX%2BHo%2B0rdaLNYGeRNg%3D");
        request.setUniqueId("123456");

        when(azureBlobService.uploadFile(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture("OK: Object already exists"));

        CompletableFuture<ResponseEntity<ApiResponse>> response = fileUploadController.uploadFile(request);

        assertEquals(OK, response.join().getStatusCode());
        assertEquals("OK: Object already exists", response.join().getBody().getMessage());
    }
}
