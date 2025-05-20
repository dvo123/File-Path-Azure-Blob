package com.example.azureuploaddemo.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AzureBlobServiceTest {

    @InjectMocks
    private AzureBlobService azureBlobService;

    @Mock
    private BlobClientBuilder blobClientBuilder;

    @Mock
    private BlobClient blobClient;

    private static final String FILE_PATH = "testfile.txt";
    private static final String SAS_URL = "https://fsgsealrsgnrsta.blob.core.windows.net/ntg?sv=2023-01-03&st=2025-03-11T03%3A09%3A36Z&se=2025-03-15T03%3A09%3A00Z&sr=c&sp=racwl&sig=DDeP19gRstAEjHao8YC%2FUOdrxyX%2BHo%2B0rdaLNYGeRNg%3D";
    private static final String UNIQUE_ID = "1234";

    @BeforeEach
    void setUp() {
        // Ensure the file exists for testing
        File testFile = new File(FILE_PATH);
        assertTrue(testFile.exists(), "Test file does not exist. Create 'testfile.txt' in src/test/resources.");
    }

    @Test
    void testUploadFile_Success() {
        when(blobClientBuilder.endpoint(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.sasToken(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.containerName(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.blobName(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.buildClient()).thenReturn(blobClient);

        // Simulate a non-existing blob (allow upload)
        when(blobClient.getProperties()).thenThrow(new RuntimeException("Blob not found"));

        doNothing().when(blobClient).uploadFromFile(FILE_PATH, true);

        CompletableFuture<String> result = azureBlobService.uploadFile(FILE_PATH, SAS_URL, UNIQUE_ID);
        assertEquals("File uploaded successfully: " + UNIQUE_ID + "_testfile.txt", result.join());
    }

    @Test
    void testUploadFile_AlreadyExists() {
        when(blobClientBuilder.endpoint(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.sasToken(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.containerName(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.blobName(anyString())).thenReturn(blobClientBuilder);
        when(blobClientBuilder.buildClient()).thenReturn(blobClient);

        // Simulate existing blob (prevent duplicate upload)
        when(blobClient.getProperties()).thenReturn(mock(BlobProperties.class));

        CompletableFuture<String> result = azureBlobService.uploadFile(FILE_PATH, SAS_URL, UNIQUE_ID);
        assertEquals("OK: Object already exists", result.join());
    }

    @Test
    void testUploadFile_InvalidSasUrl() {
        String invalidSasUrl = "invalid-url";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> azureBlobService.uploadFile(FILE_PATH, invalidSasUrl, UNIQUE_ID));

        assertEquals("Invalid SAS URL format. Please check the URL and try again.", exception.getMessage());
    }

    @Test
    void testUploadFile_FileNotFound() {
        String invalidFilePath = "src/test/resources/nonexistent.txt";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> azureBlobService.uploadFile(invalidFilePath, SAS_URL, UNIQUE_ID));

        assertEquals("File does not exist: " + invalidFilePath, exception.getMessage());
    }
}
