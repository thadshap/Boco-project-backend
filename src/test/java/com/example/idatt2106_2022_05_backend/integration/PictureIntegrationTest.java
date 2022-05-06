package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.*;

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
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Nested
    class UserPictureTests {

        @SneakyThrows
        @BeforeEach
        public void setUp() {
            // Building a user
            User user = User.builder().firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                    .password("pass1word").build();

            // Saving the user
            userRepository.save(user);

            // Building categories
            Category clothes = Category.builder().name("new category1").parent(true).build();

            Category it = Category.builder().name("new category2").parent(true).build();

            // Saving the categories
            categoryRepository.save(clothes);
            categoryRepository.save(it);

            // Create ads as well
            AdDto speaker1 = AdDto.builder().title("New speaker").description("Renting out a brand new speaker")
                    .rental(true).durationType(AdType.WEEK).price(100).streetAddress("Speaker street 2")
                    .postalCode(7120).city("Trondheim").userId(user.getId()).categoryId(it.getId()).build();

            // persist ad
            adService.postNewAd(speaker1);
        }

        @AfterEach
        public void emptyDatabase() {
            List<Picture> pictures = pictureRepository.findAll();
            for (Picture picture : pictures) {
                if(picture.getUser() != null) {
                    picture.getUser().setPicture(null);
                    userRepository.save(picture.getUser());
                    picture.setUser(null); // todo do the same with user?
                    pictureRepository.save(picture);
                }
            }
            pictureRepository.deleteAll();
        }

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
            MultipartFile mockMultipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

            try {
                // Perform the method
                ResponseEntity<Object> res =
                        userService.updatePicture(user.getId(), mockMultipartFile);

                // Assert that the profile picture exists
                Optional<User> userFound = userRepository.findById(user.getId());
                if (userFound.isPresent()) {
                    assertNotNull(userFound.get().getPicture());
                    assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
                } else {
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
            MultipartFile mockMultipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

            // Perform the method using the wrong user id (non-existent user)
            try {
                long wrongUserId = 100010L;
                // Perform the method
                ResponseEntity<Object> res =
                        userService.updatePicture(wrongUserId, mockMultipartFile);

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
            assertTrue(userRepository.findAll().size() > 0);
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
            MultipartFile mockMultipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

            try {
                // Save the picture
                ResponseEntity<Object> res =
                        userService.updatePicture(user.getId(), mockMultipartFile);

                // Assert that the profile picture exists
                Optional<User> userFound = userRepository.findById(user.getId());
                if (userFound.isPresent()) {
                    System.out.println("got here");
                    assertNotNull(userFound.get().getPicture());
                    assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());

                    // Now, delete the profile picture
                    ResponseEntity<Object> res2 = userService.deleteProfilePicture(userFound.get().getId(),
                            mockMultipartFile.getBytes());

                    // Assert the correct response
                    assertEquals(res2.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());

                    // Get the user
                    Optional<User> userFound2 = userRepository.findById(user.getId());
                    assertNotNull(userFound2.get().getPicture());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    class AdPictureTests {

        @SneakyThrows
        @BeforeEach
        public void setUp() {
            // Building a user
            User user = User.builder().firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                    .password("pass1word").build();

            // Saving the user
            userRepository.save(user);

            // Building categories
            Category clothes = Category.builder().name("new category1").parent(true).build();

            Category it = Category.builder().name("new category2").parent(true).build();

            // Saving the categories
            categoryRepository.save(clothes);
            categoryRepository.save(it);

            // Create ads as well
            AdDto speaker1 = AdDto.builder().title("New speaker").description("Renting out a brand new speaker")
                    .rental(true).durationType(AdType.WEEK).price(100).streetAddress("Speaker street 2")
                    .postalCode(7120).city("Trondheim").userId(user.getId()).categoryId(it.getId()).build();

            // persist ad
            adService.postNewAd(speaker1);
        }

        @AfterEach
        public void emptyDatabase() {
            reviewRepository.deleteAll();
            rentalRepository.deleteAll();
            messageRepository.deleteAll();
            adRepository.deleteAll();
            pictureRepository.deleteAll();
            userRepository.deleteAll();
            // userRepository.deleteAll();
            // adRepository.deleteAll();
            // adRepository.deleteAll();
            // userRepository.deleteAll();
            categoryRepository.deleteAll();
            calendarDateRepository.deleteAll();
        }

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
            MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

            // Put the multipartFile into a set
            List<MultipartFile> files = new ArrayList<>();
            files.add(result);

            // Perform the method
            try {
                // TODO method is in adservice

                ResponseEntity<Object> res = adService.storeImageForAd(ad.getId(), files);

                // Assert that the profile picture exists
                Optional<Ad> adFound = adRepository.findById(ad.getId());
                if (adFound.isPresent()) {
                    assertNotNull(adFound.get().getPictures());
                    assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());

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
            MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

            // Put the multipartFile into a set
            List<MultipartFile> files = new ArrayList<>();
            files.add(result);

            // Perform the method using the wrong user id (non-existent user)
            try {
                long wrongAdId = 100010L;
                ResponseEntity<Object> res = adService.storeImageForAd(wrongAdId, files);

                // Assert that the profile picture does not exist
                Optional<Ad> adFound = adRepository.findById(wrongAdId);
                assertFalse(adFound.isPresent());
                assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Remove photo from ad
        @SneakyThrows
        @Test
        public void pictureDeletedFromAd() {
            int adsInRepo = adRepository.findAll().size();
            assertTrue(adsInRepo > 0);
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
            MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

            // Put the multipartFile into a set
            List<MultipartFile> files = new ArrayList<>();
            files.add(result);

            // Perform the method to save the picture
            try {

                ResponseEntity<Object> res = adService.storeImageForAd(ad.getId(), files);

                // Assert that the profile picture exists
                Optional<Ad> adFound = adRepository.findById(ad.getId());
                if (adFound.isPresent()) {

                    // Assert that the ad found has pictures
                    assertNotNull(adFound.get().getPictures());

                    // Retrieve the new number of pictures for the ad
                    int currentNumberOfPictures = adFound.get().getPictures().size();

                    // Assert that the correct code is returned
                    assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());

                    // Assert that there is now one more picture than previously connected to the ad
                    assertNotEquals(numberOfPictures, currentNumberOfPictures);

                    // Now, delete the picture
                    ResponseEntity<Object> response = adService.deletePicture(adFound.get().getId(),
                            files);

                    Optional<Ad> adFound2 = adRepository.findById(ad.getId());
                    if (adFound.isPresent()) {

                        // Assert correct response
                        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
                        assertEquals(1, adFound2.get().getPictures().size());
                    }
                    else {
                        fail();
                    }
                } else {
                    fail();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
