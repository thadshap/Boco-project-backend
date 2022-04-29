package com.example.idatt2106_2022_05_backend.integration;


import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PictureIntegrationTest {


    @Autowired
    AdService adService;
    @Autowired
    AdRepository adRepository;

    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {

    }

    @Nested
    class UserPictureTests {
        // Add photo to user
        @Test
        public void profilePictureAdded_WhenCorrectInput() {
            // Get an existing user
            // Verify that the user exists
            // Create a UserUpdateDto with the picture
            // Perform the method
            // Assert that the profile picture exists
        }

        @Test
        public void profilePictureNotAdded_WhenWrongInput() {

        }

        // Remove photo from user
        @Test
        public void pictureDeleted() {

        }

        @Test
        public void pictureNotDeleted() {

        }
    }

    @Nested
    class AdPictureTests {

        // Add photo to ad
        @Test
        public void pictureAdded_WhenCorrectInput() {

        }

        @Test
        public void pictureNotAdded_WhenWrongInput() {

        }

        // Remove photo from ad
        @Test
        public void pictureDeletedFromAd() {

        }

        @Test
        public void pictureNotDeletedFromAd() {

        }

    }

}
