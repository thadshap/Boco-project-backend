package com.example.idatt2106_2022_05_backend.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JWTResponse {

    @NonNull
    private String accessToken;
    private String tokenType = "Bearer";
}
