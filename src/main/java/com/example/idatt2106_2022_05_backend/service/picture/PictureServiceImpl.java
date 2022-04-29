package com.example.idatt2106_2022_05_backend.service.picture;

import com.example.idatt2106_2022_05_backend.dto.PictureDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
@Service
public class PictureServiceImpl implements PictureService{

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    AdRepository adRepository;

    /**
     * TODO
     * @param id
     * @return
     */
    private Picture getPictureById(long id){
        return pictureRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "fant ikke bildet"));
    }

    /**
     * TODO
     * @param id
     * @return
     */
    private Ad getAd(long id){
        return adRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "fant ikke annonsen"));
    }

    /**
     * TODO
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public Response getPicture(long id) throws IOException {
        Picture picture = getPictureById(id);
        PictureDto pictureDto = new PictureDto();

        ByteArrayInputStream bis = new ByteArrayInputStream(PictureUtility.decompressImage(picture.getContent()));
        Image image = ImageIO.read(bis);
        pictureDto.set(image);
        pictureDto.setFilename(picture.getFilename());
        return new Response(pictureDto, HttpStatus.OK);
    }

    /**
     * TODO
     * @param file
     * @param adId
     * @return
     * @throws IOException
     */
    @Override
    public Response postNewPicture(MultipartFile file, long adId) throws IOException {
        Ad ad = getAd(adId);

        Picture picture = Picture.builder()
                .type(file.getContentType())
                .filename(file.getOriginalFilename())
                .ad(ad).content(PictureUtility.compressImage(file.getBytes())).build();
        ad.getPictures().add(picture);
        return new Response("Opplasting av bildet vellykket", HttpStatus.OK);
    }

    /**
     * TODO
     * @param filename
     * @return
     */
    @Override
    public Response deletePicture(String filename){
        Picture picture = pictureRepository.findByFilename(filename)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "fant ikke bildet"));
        pictureRepository.delete(picture);
        return new Response("bildet ble slettet", HttpStatus.OK);
    }
}
 */
