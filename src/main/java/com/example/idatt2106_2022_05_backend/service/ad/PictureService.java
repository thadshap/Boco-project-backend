package com.example.idatt2106_2022_05_backend.service.ad;

import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.util.PictureUploadResponse;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
public class PictureService {

    @Autowired
    PictureRepository pictureRepository;

    public ResponseEntity<PictureUploadResponse> uploadPicture(MultipartFile file) throws Exception{
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "File is empty");
        }
        pictureRepository.save(Picture.builder()
                .filename(file.getOriginalFilename())
                .type(file.getContentType())
                .content(PictureUtility.compressImage(file.getBytes())).build());
        return ResponseEntity.status(HttpStatus.OK).body(new PictureUploadResponse("Image uploaded successfully" + file.getOriginalFilename()));
    }

    public Picture getPictureByFilename(String filename){
        Optional<Picture> picture = pictureRepository.findByName(filename);
        if(!picture.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No picture with this filename found");
        }
        return Picture.builder()
                .filename(picture.get().getFilename())
                .type(picture.get().getType())
                .content(PictureUtility.decompressImage(picture.get().getContent())).build();
    }
}
