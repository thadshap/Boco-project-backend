package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.service.PictureService;
import com.example.idatt2106_2022_05_backend.util.PictureUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@RestController
@CrossOrigin("http://localhost:8081")
@RequestMapping("/api")
public class PictureController {

    @Autowired
    PictureService pictureService;

    @PostMapping("/upload/image")
    public ResponseEntity<PictureUploadResponse> uploadImage(@RequestParam("image") MultipartFile file) throws Exception {
        return pictureService.uploadPicture(file);
    }

    @GetMapping("/getimage/{filename}")
    public Image getPicture(@PathVariable("name")String filename) throws Exception{

    }


}
