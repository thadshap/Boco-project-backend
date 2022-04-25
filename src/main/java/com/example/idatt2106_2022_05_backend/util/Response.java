package com.example.idatt2106_2022_05_backend.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class Response extends ResponseEntity<Object> {

    private Object object;
    private HttpStatus status;

    public Response(Object body, HttpStatus status) {
        super(body, status);
    }
}
