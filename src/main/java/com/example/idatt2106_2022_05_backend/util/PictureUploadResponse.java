package com.example.idatt2106_2022_05_backend.util;

public class PictureUploadResponse {
    private String message;

    public PictureUploadResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
