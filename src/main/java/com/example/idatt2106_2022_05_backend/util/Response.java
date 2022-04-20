package com.example.idatt2106_2022_05_backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response extends ResponseEntity<Object> {

    public Response(Object body, HttpStatus status) {
        super(body, status);
    }
}
