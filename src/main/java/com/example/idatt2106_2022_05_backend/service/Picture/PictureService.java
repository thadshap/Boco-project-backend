package com.example.idatt2106_2022_05_backend.service.Picture;

import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface PictureService {

    Response getPicture(long id) throws IOException;

    Response postNewPicture(MultipartFile file, long adId) throws IOException;

    Response deletePicture(String filename);
}
