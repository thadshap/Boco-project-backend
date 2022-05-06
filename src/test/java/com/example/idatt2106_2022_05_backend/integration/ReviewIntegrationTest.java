package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.review.ReviewService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.mysql.cj.xdevapi.SessionFactory;
import lombok.SneakyThrows;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ReviewIntegrationTest {

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
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewService reviewService;

    @Nested
    class PositiveReviewTests {
        // Write review correct check OK
        // check repo count
        @SneakyThrows
        @Test
        public void reviewSaved_WhenForeignKeysCorrect() {
            // Create a user
            User user1 = User.builder().firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no")
                    .password("passord123").build();

            User user = userRepository.save(user1);

            // Assert that they exist
            assertNotNull(user);

            // Create a category
            Category category1 = Category.builder().name("category").parent(true).child(false).build();
            Category category = categoryRepository.save(category1);

            // Create new ad
            Ad speaker = Ad.builder().title("New speaker").description("Renting out a brand new speaker").rental(true)
                    .durationType(AdType.WEEK).price(100).streetAddress("Speaker street 2").postalCode(7120)
                    .city("Trondheim").user(user).category(category).build();

            // Persist the ad --> the dates are now also persisted
            Ad ad = adRepository.save(speaker);

            // Get the number of reviews in the database
            int prevNumberOfReviewsInDb = reviewRepository.findAll().size();

            // Set as 0
            int prevNumberOfReviewsInAd = 0;

            // Get the number of reviews in the ad (unless null)
            if (ad.getReviews() != null) {
                prevNumberOfReviewsInAd = ad.getReviews().size();
            }

            // Create Review dto entity
            ReviewDto review = ReviewDto.builder().description("Great shoes!").rating(5).adId(ad.getId())
                    .userId(user.getId()).build();

            // Save the review entity
            ResponseEntity<Object> res = reviewService.createNewReview(review);

            // Assert that the db increased with one review
            assertNotEquals(prevNumberOfReviewsInDb, reviewRepository.findAll().size());

            // Assert that the ads reviews increased with one review
            assertNotEquals(prevNumberOfReviewsInAd, adRepository.findById(ad.getId()).get().getReviews().size());

            // Assert that the status code was correct
            assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
        }
    }

    @Nested
    class NegativeReviewTests {
        // Write review without user check NOT found
        @Test
        public void reviewNotSaved_WhenForeignKeysWrong() {

            // Get the number of reviews in the database
            int prevNumberOfReviewsInDb = reviewRepository.findAll().size();

            // Create Review dto entity without fk
            ReviewDto review = ReviewDto.builder().description("Great shoes!").rating(5).build();

            // Save the review entity
            try {
                ResponseEntity<Object> res = reviewService.createNewReview(review);
            } catch (LazyInitializationException e) {
                // Pass

                // Assert that the db did not increase with one review
                assertEquals(prevNumberOfReviewsInDb, reviewRepository.findAll().size());
            }
        }
    }
}
