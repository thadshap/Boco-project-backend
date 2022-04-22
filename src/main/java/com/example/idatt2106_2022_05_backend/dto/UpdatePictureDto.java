package com.example.idatt2106_2022_05_backend.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdatePictureDto {
    private long ad_id;
    private long picture_id;
    private MultipartFile file;
}
