package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureDto{

    private String filename;
    private Image file;

}
