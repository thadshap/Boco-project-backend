package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarServiceImpl;
import lombok.SneakyThrows;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
    CalendarService calendarService;

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

        @SneakyThrows
        @Test
        public void datesAreAvailable_WhenCreated() {
            // Find a user and category
            User user  = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Create new ad
            AdDto speaker = AdDto.builder().
                    title("New speaker").
                    description("Renting out a brand new speaker").
                    rental(true).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(100).
                    streetAddress("Speaker street 2").
                    postalCode(7120).
                    city("Trondheim").
                    userId(user.getId()).
                    categoryId(category.getId()).
                    build();

            // Persist the ad --> the dates are now also persisted
            adService.postNewAd(speaker);
            Set<Ad> adsFound = adRepository.findByTitleContaining("New speaker");
            assertEquals(adsFound.size(),1);

            // Verify that the dates were created
            Ad ad = adsFound.stream().findFirst().get();

            // Assert that the ad exists
            assertNotNull(ad);
            assertNotEquals(ad.getId(), 0);
            assertEquals(LocalDate.EPOCH.lengthOfYear(), ad.getDates().size());

            // Assert that the dates are available using CalendarService support method
            CalendarDto dtoMock = CalendarDto.builder().
                    adId(ad.getId()).
                    available(false).
                    startDate(LocalDate.now()).
                    endDate(LocalDate.now().plusYears(1)).
                    build();

            // Call on the method
            ResponseEntity<Object> res = calendarService.getUnavailableDates(dtoMock);

            // Assert HTTP-response  true
            assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
        }


        @Test
        public void datesAreMadeUnavailable() {
            Ad ad = adRepository.findAll().get(0);
            assertNotNull(ad);

            // Get number of unavailable dates for the ad
            int unavailableBefore = 0;
            Set<CalendarDate> dates = ad.getDates();
            for (CalendarDate date : dates) {
                if(!date.isAvailable()) {
                    unavailableBefore ++;
                }
            }

            // Get the date of creation for the ad
            LocalDate created = ad.getCreated();

            // Get a random date... say, two weeks after creation
            LocalDate afterCreation = created.plusWeeks(2);

            // Set a week of dates as unavailable
            CalendarDto dtoMock = CalendarDto.builder().
                    adId(ad.getId()).
                    startDate(afterCreation).
                    endDate(afterCreation.plusWeeks(1)).
                    available(false).
                    build();

            // Mark the week as unavailable (rented out)
            calendarService.markDatesFromToAs(dtoMock);

            Ad adAfter = adRepository.findAll().get(0);

            // Get a count of how many dates are unavailable for the ad now
            int unavailableAfter = 0;
            for(CalendarDate date : adAfter.getDates()) {
                if(!date.isAvailable()) {
                    unavailableAfter ++;
                }
            }

            // There should now be 7 more unavailable dates
            assertNotEquals(unavailableAfter, 0);
            assertEquals(unavailableBefore, 0);
            assertEquals(unavailableAfter, 7);
            assertNotEquals(unavailableBefore, unavailableAfter);
            assertEquals(unavailableAfter, unavailableBefore + 7);


        }

        @Test
        public void trueReturned_WhenAllDatesAvailable() {

        }

        @Test
        public void falseReturned_WhenNotAllDatesAvailable() {

        }
    }

}
