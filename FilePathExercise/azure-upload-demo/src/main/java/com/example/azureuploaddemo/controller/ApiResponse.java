package com.example.azureuploaddemo.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from response
public class ApiResponse {
    private final boolean success;
    private final String message;
    private final String error;

    public ApiResponse(@JsonProperty("success") boolean success, 
                       @JsonProperty("message") String message, 
                       @JsonProperty("error") String error) {
        this.success = success;
        this.message = message;
        this.error = error;
    }

    @JsonProperty
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public String getError() {
        return error;
    }
}
