package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureReturnDto {

    public Long id;

    private String type;

    private String base64;
}
