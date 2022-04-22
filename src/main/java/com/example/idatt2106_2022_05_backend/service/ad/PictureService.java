package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.util.PictureUploadResponse;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface PictureService {

    public ResponseEntity<PictureUploadResponse> uploadPicture(MultipartFile file) throws Exception;

}
