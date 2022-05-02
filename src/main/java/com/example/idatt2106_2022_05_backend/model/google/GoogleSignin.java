package com.example.idatt2106_2022_05_backend.model.google;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleSignin {

    private String id_token;
}
