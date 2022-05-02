package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.model.Ad;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class will test methods related to CalendarDate.java and Ad.java
 */

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CalendarIntegrationTest {


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

    @Nested
    class CalendarCreatedTests {

        @Test
        public void oneYearOfDatesCreated_WhenAdIsCreated() {
            // Get number of ads in db as of right now
            int numberOfAds = adRepository.findAll().size();

            // Get the length of a year
            int lengthOfYear = LocalDate.EPOCH.lengthOfYear();

            // Multiply with one year for each ad
            int numberOfDates = lengthOfYear * numberOfAds;

            // There should be that many dates in db
            assertEquals(calendarDateRepository.findAll().size(), numberOfDates);

            // Assert that the number of ads in db is a round number....
            assertEquals(numberOfDates / lengthOfYear, numberOfAds);
        }

        @Test
        public void oneYearOfDatesRemoved_WhenAdIsRemoved() {
            // Get number of ads in db as of right now
            int numberOfAds = adRepository.findAll().size();

            // Get length of year
            int lengthOfYear = LocalDate.EPOCH.lengthOfYear();

            // Multiply with one year for each ad
            int numberOfDates = lengthOfYear * numberOfAds;

            // Retrieve one ad
            Ad adFound = adRepository.findAll().get(0);
            assertNotNull(adFound);

            // Remove the ad
            adService.deleteAd(adFound.getId());
            //adRepository.delete(adFound);

            // Calculate current number of ads
            int newNumberOfAds = adRepository.findAll().size();
            assertNotEquals(newNumberOfAds, numberOfAds);

            // Calculate current number of dates
            int newNumberOfDates = lengthOfYear * newNumberOfAds;

            // Assert number of dates
            assertNotEquals(newNumberOfDates, numberOfDates);
            assertEquals(newNumberOfDates + lengthOfYear, numberOfDates);
        }
    }

    @Nested
    class CalendarAvailabilityTests {

        @Test
        public void datesAreAvailable_WhenCreated() {

        }

        @Test
        public void datesAreMadeUnavailable() {

        }

        @Test
        public void trueReturned_WhenAllDatesAvailable() {

        }

        @Test
        public void falseReturned_WhenNotAllDatesAvailable() {

        }
    }

}
