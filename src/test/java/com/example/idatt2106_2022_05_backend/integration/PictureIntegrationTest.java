package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PictureIntegrationTest {


    @Autowired
    AdService adService;

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserService userService;

    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PictureUtility pictureService;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Nested
    class UserPictureTests {
        // Add photo to user
        @Test
        public void profilePictureAdded_WhenCorrectInput() {
            // Get an existing user
            User user = userRepository.findAll().get(0);

            // Verify that the user exists
            assertNotNull(user);

            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method
            try {
                ResponseEntity<Object> res = pictureService.savePicture(result,0, user.getId());

                // Assert that the profile picture exists
                Optional<User> userFound = userRepository.findById(user.getId());
                if(userFound.isPresent()) {
                    assertNotNull(userFound.get().getPicture());
                    assertEquals(HttpStatus.CREATED.value(), res.getStatusCodeValue());
                }
                else {
                    fail();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Test
        public void profilePictureNotAdded_WhenWrongInput() {

            // The path to the picture (image)
            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method using the wrong user id (non-existent user)
            try {
                long wrongUserId = 100010L;
                ResponseEntity<Object> res = pictureService.savePicture(result,0, wrongUserId);

                // Assert that the profile picture does not exist
                Optional<User> userFound = userRepository.findById(wrongUserId);
                assertFalse(userFound.isPresent());
                assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Remove photo from user
        @Test
        public void pictureDeleted() {
            // Get an existing user
            User user = userRepository.findAll().get(0);

            // Verify that the user exists
            assertNotNull(user);

            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile mockMultipartFile = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method
            try {
                ResponseEntity<Object> res = pictureService.savePicture(mockMultipartFile,0, user.getId());

                // Assert that the profile picture exists
                Optional<User> userFound = userRepository.findById(user.getId());
                if(userFound.isPresent()) {
                    assertNotNull(userFound.get().getPicture());
                    assertEquals(HttpStatus.CREATED.value(), res.getStatusCodeValue());

                    // Now, delete the profile picture
                    ResponseEntity<Object> res2 = userService.
                            deleteProfilePicture(user.getId(), mockMultipartFile.getBytes());

                    // Assert the correct response
                    assertEquals(res2.getStatusCodeValue(), HttpStatus.OK.value());
                }
                else {
                    fail();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    class AdPictureTests {

        // Add photo to ad
        @Test
        public void pictureAdded_WhenCorrectInput() {
            // Get an existing ad
            Ad ad = adRepository.findAll().get(0);

            // Verify that the user exists
            assertNotNull(ad);

            // Get the number of pictures for the ad
            Set<Picture> pictures = ad.getPictures();
            int numberOfPictures = pictures.size();

            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method
            try {

                ResponseEntity<Object> res = pictureService.savePicture(result, ad.getId(), 0);

                // Assert that the profile picture exists
                Optional<Ad> adFound = adRepository.findById(ad.getId());
                if (adFound.isPresent()) {
                    assertNotNull(adFound.get().getPictures());
                    assertEquals(HttpStatus.CREATED.value(), res.getStatusCodeValue());

                    // Assert that there is now one more picture than previously connected to the ad
                    assertNotEquals(numberOfPictures, adFound.get().getPictures().size());
                } else {
                    fail();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Test
        public void pictureNotAdded_WhenWrongInput() {
            // The path to the picture (image)
            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method using the wrong user id (non-existent user)
            try {
                long wrongAdId = 100010L;
                ResponseEntity<Object> res = pictureService.savePicture(result, wrongAdId, 0);

                // Assert that the profile picture does not exist
                Optional<Ad> adFound = adRepository.findById(wrongAdId);
                assertFalse(adFound.isPresent());
                assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Remove photo from ad
        @Test
        public void pictureDeletedFromAd() {
            // Get an existing ad
            Ad ad = adRepository.findAll().get(0);

            // Verify that the user exists
            assertNotNull(ad);

            // Get the number of pictures for the ad
            Set<Picture> pictures = ad.getPictures();
            int numberOfPictures = pictures.size();

            Path path = Paths.get("src/test/resources/ImageForTesting/Eivind_Hellstrom.jpg");
            String name = "eh.txt";
            String originalFileName = "Eivind_Hellstrom.jpg";
            String contentType = "image/jpeg";
            byte[] content = null;

            try {
                content = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Multipart file is mocked
            MultipartFile result = new MockMultipartFile(name,
                    originalFileName, contentType, content);


            // Perform the method to save the picture
            try {

                ResponseEntity<Object> res = pictureService.savePicture(result, ad.getId(), 0);

                // Assert that the profile picture exists
                Optional<Ad> adFound = adRepository.findById(ad.getId());
                if (adFound.isPresent()) {

                    // Assert that the ad found has pictures
                    assertNotNull(adFound.get().getPictures());

                    // Retrieve the new number of pictures for the ad
                    int currentNumberOfPictures = adFound.get().getPictures().size();

                    // Assert that the correct code is returned
                    assertEquals(HttpStatus.CREATED.value(), res.getStatusCodeValue());

                    // Assert that there is now one more picture than previously connected to the ad
                    assertNotEquals(numberOfPictures, currentNumberOfPictures);

                    // Now, delete the picture


                } else {
                    fail();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
