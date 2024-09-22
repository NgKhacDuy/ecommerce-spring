package com.app.config;

import org.springframework.stereotype.Component;

@Component
public class ApiResponse<T> {
    private T data;
    private String message;

    public ApiResponse() {
        // Default constructor for autowiring
    }

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}