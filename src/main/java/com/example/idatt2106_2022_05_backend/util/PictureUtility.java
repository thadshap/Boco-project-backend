package com.example.idatt2106_2022_05_backend.util;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class PictureUtility {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PictureRepository pictureRepository;

    public static byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
        }
        return outputStream.toByteArray();
    }

    /**
     * Support-method to create and save Picture.
     * To use, set either adId or userId equal to the desired id.
     * The other id must be set to 0.
     */
    public Response savePicture(MultipartFile file, long adId, long userId) throws IOException {

        // Ensures that content of multipartFile is present
        if (file.isEmpty()) {
            return new Response("Picture multipartFile is empty", HttpStatus.NO_CONTENT);
        }
        // If this is true an exception is returned (only one at a time)
        if (adId < 0 && userId < 0) {
            return new Response("Both ad and user cannot be edited at the same time",
                    HttpStatus.NOT_FOUND);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Create Picture entity
        Picture filePicture = new Picture();

        // This is true if we are adding an image to an ad
        if (adId > 0) {
            // Find the ad
            Optional<Ad> adFound = adRepository.findById(adId);

            if (adFound.isPresent()) {

                // Set attributes for the new entity
                filePicture.setFilename(fileName);
                filePicture.setType(file.getContentType());
                filePicture.setData(file.getBytes());

                // Set the ad as FK
                filePicture.setAd(adFound.get());

                // Persist the Picture
                pictureRepository.save(filePicture);

                // Add the Picture as FK to the ad as well
                adFound.get().addPicture(filePicture);

                // Persist the ad
                adRepository.save(adFound.get());

                // Return OK
                return new Response("Successfully added new photo to ad", HttpStatus.CREATED);
            } else {
                return new Response("Could not find ad with specified id", HttpStatus.NOT_FOUND);
            }
        }

        // This is true if we are saving a profile picture
        if (userId > 0) {
            // Find the ad
            Optional<User> userFound = userRepository.findById(userId);

            if(userFound.isPresent()) {

                // Set attributes for the new entity
                filePicture.setFilename(fileName);
                filePicture.setType(file.getContentType());
                filePicture.setData(file.getBytes());

                // Set the ad as FK
                filePicture.setUser(userFound.get());

                // Persist the Picture
                pictureRepository.save(filePicture);

                // Add the Picture as FK to the ad as well
                userFound.get().setPicture(filePicture);

                // Persist the ad
                userRepository.save(userFound.get());

                // Return OK
                return new Response("Successfully added new photo to ad", HttpStatus.CREATED);
            }
            else {
                return new Response("Could not find ad with specified id", HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }
}
