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
import com.example.idatt2106_2022_05_backend.util.Response;
import lombok.SneakyThrows;
import org.apache.tomcat.jni.Local;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
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

    @Autowired
    PictureRepository pictureRepository;

    @Nested
    class CalendarCreatedTests {

        @SneakyThrows
        @BeforeEach
        public void setUp() {
            // Deleting due to threads
            reviewRepository.deleteAll();
            rentalRepository.deleteAll();
            pictureRepository.deleteAll();
            adRepository.deleteAll();
            userRepository.deleteAll();
            categoryRepository.deleteAll();
            calendarDateRepository.deleteAll();

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
                    .rental(true).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Speaker street 2")
                    .postalCode(7120).lat(63.401920).lng(10.443579).city("Trondheim").userId(user.getId())
                    .categoryId(it.getId()).build();

            // persist ad
            adService.postNewAd(speaker1);
        }

        @AfterEach
        public void emptyDatabase() {
            reviewRepository.deleteAll();
            rentalRepository.deleteAll();
            adRepository.deleteAll();
            userRepository.deleteAll();
            categoryRepository.deleteAll();
            calendarDateRepository.deleteAll();
        }

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
            // adRepository.delete(adFound);

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
            // Create a user
            User user1 = User.builder().firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no")
                    .password("passord123").build();

            User user = userRepository.save(user1);

            // Create a category
            Category category1 = Category.builder().name("category").parent(true).child(false).build();
            Category category = categoryRepository.save(category1);

            // Create new ad
            AdDto speaker = AdDto.builder().title("new ad for category").description("Renting out a brand new speaker")
                    .rental(true).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Speaker street 2")
                    .postalCode(7120).city("Trondheim").lat(63.401920).lng(10.443579).userId(user.getId())
                    .categoryId(category.getId()).build();

            // Persist the ad --> the dates are now also persisted
            adService.postNewAd(speaker);
            Set<Ad> adsFound = adRepository.findByTitleContaining("new ad for category");
            assertEquals(adsFound.size(), 1);

            // Verify that the dates were created
            Ad ad = adsFound.stream().findFirst().get();

            // Assert that the ad exists
            assertNotNull(ad);
            assertNotEquals(ad.getId(), 0);
            assertEquals(LocalDate.EPOCH.lengthOfYear(), ad.getDates().size());

            // Assert that the dates are available using CalendarService support method
            CalendarDto dtoMock = CalendarDto.builder().adId(ad.getId()).available(false).startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1)).build();

            // Call on the method
            ResponseEntity<Object> res = calendarService.getUnavailableDates(dtoMock);

            // Assert HTTP-response true
            assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
        }

        @SneakyThrows
        @Test
        public void datesAreMadeUnavailable() {
            // Create a user
            User user1 = User.builder().firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no")
                    .password("passord123").build();

            User user = userRepository.save(user1);

            // Create a category
            Category category1 = Category.builder().name("category").parent(true).child(false).build();
            Category category = categoryRepository.save(category1);

            // Create new ad
            AdDto speaker = AdDto.builder().title("New ad for testing and such")
                    .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                    .price(100).streetAddress("Speaker street 2").postalCode(7120).city("Trondheim").lat(63.401920)
                    .lng(10.443579).userId(user.getId()).categoryId(category.getId()).build();

            // Persist the ad --> the dates are now also persisted
            ResponseEntity<Object> r = adService.postNewAd(speaker);

            assertEquals(HttpStatus.CREATED.value(), r.getStatusCodeValue());

            // Use the method to search through all ads
            ResponseEntity<Object> response = adService.searchThroughAds("New ad for testing and such");
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

            Set<Ad> adsFound = adRepository.findByTitle("New ad for testing and such");

            assertNotNull(adsFound);

            assertEquals(1, adsFound.size());

            Ad ad = adsFound.stream().findFirst().get();

            // Assert that the ad was actually found
            assertNotNull(ad);

            // Assert that the ad now has 365 dates
            assertEquals(365, ad.getDates().size());

            // Get number of unavailable dates for the ad
            int unavailableBefore = 0;
            Set<CalendarDate> dates = ad.getDates();
            for (CalendarDate date : dates) {
                if (!date.isAvailable()) {
                    unavailableBefore++;
                }
            }

            // Get the date of creation for the ad
            LocalDate created = ad.getCreated();

            // Get a random date... say, two weeks after creation
            LocalDate afterCreation = created.plusWeeks(2);

            // Set a week of dates as unavailable
            CalendarDto dtoMock = CalendarDto.builder().adId(ad.getId()).startDate(afterCreation)
                    .endDate(afterCreation.plusDays(6)).available(false).build();

            // Mark the week as unavailable (rented out)
            ResponseEntity<Object> res = calendarService.markDatesFromToAs(dtoMock);

            // Assert that the correct HTTP-response was received before proceeding
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());

            Optional<Ad> adAfter = adRepository.findById(ad.getId());
            // Ad adAfter = adRepository.findAll().get(0);

            // Get a count of how many dates are unavailable for the ad now
            int unavailableAfter = 0;
            Set<CalendarDate> dates2 = adAfter.get().getDates();
            for (CalendarDate date : dates2) {
                if (!date.isAvailable()) {
                    unavailableAfter++;
                }
            }

            // There should now be 7 more unavailable dates
            assertNotEquals(unavailableAfter, 0);
            assertEquals(unavailableBefore, 0);
            assertEquals(unavailableAfter, 7);
            assertNotEquals(unavailableBefore, unavailableAfter);
            assertEquals(unavailableAfter, unavailableBefore + 7);
        }

        @SneakyThrows
        @Test
        public void getUnavailableDatesForAd() {
            // Create a user
            User user1 = User.builder().firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no")
                    .password("passord123").build();

            User user = userRepository.save(user1);

            // Create a category
            Category category1 = Category.builder().name("category").parent(true).child(false).build();
            Category category = categoryRepository.save(category1);

            // Create new ad
            AdDto speaker = AdDto.builder().title("New ad for testing").description("Renting out a brand new speaker")
                    .rental(true).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Speaker street 2")
                    .postalCode(7120).city("Trondheim").lat(63.401920).lng(10.443579).userId(user.getId())
                    .categoryId(category.getId()).build();

            // Persist the ad --> the dates are now also persisted
            ResponseEntity<Object> r = adService.postNewAd(speaker);

            assertEquals(HttpStatus.CREATED.value(), r.getStatusCodeValue());

            // Use the method to search through all ads
            ResponseEntity<Object> response = adService.searchThroughAds("New ad for testing");
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

            Set<Ad> adsFound = adRepository.findByTitle("New ad for testing");

            assertNotNull(adsFound);

            assertEquals(1, adsFound.size());

            Ad ad = adsFound.stream().findFirst().get();

            // Assert that the ad was actually found
            assertNotNull(ad);

            // Assert that the ad now has 365 dates
            assertEquals(365, ad.getDates().size());

            // Get number of unavailable dates for the ad
            int unavailableBefore = 0;
            Set<CalendarDate> dates = ad.getDates();
            for (CalendarDate date : dates) {
                if (!date.isAvailable()) {
                    unavailableBefore++;
                }
            }

            // Get the date of creation for the ad
            LocalDate created = ad.getCreated();

            // Get a random date... say, two weeks after creation
            LocalDate afterCreation = created.plusWeeks(2);

            // Set a week of dates as unavailable
            CalendarDto dtoMock = CalendarDto.builder().adId(ad.getId()).startDate(afterCreation)
                    .endDate(afterCreation.plusDays(6)).available(false).build();

            // Mark the week as unavailable (rented out)
            ResponseEntity<Object> res = calendarService.markDatesFromToAs(dtoMock);

            // Assert that the correct HTTP-response was received before proceeding
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());

            Optional<Ad> adAfter = adRepository.findById(ad.getId());
            // Ad adAfter = adRepository.findAll().get(0);

            // Get a count of how many dates are unavailable for the ad now
            int unavailableAfter = 0;
            Set<CalendarDate> dates2 = adAfter.get().getDates();
            for (CalendarDate date : dates2) {
                if (!date.isAvailable()) {
                    unavailableAfter++;
                }
            }

            // There should now be 7 more unavailable dates
            assertNotEquals(unavailableAfter, 0);
            assertEquals(unavailableBefore, 0);
            assertEquals(unavailableAfter, 7);
            assertNotEquals(unavailableBefore, unavailableAfter);
            assertEquals(unavailableAfter, unavailableBefore + 7);

            // Create a dto
            CalendarDto dto = CalendarDto.builder().adId(ad.getId()).build();

            // Now, we try to get the unavailable dates for this ad.
            ResponseEntity<Object> res2 = calendarService.getUnavailableDates(dto);
            ArrayList<LocalDate> datesFound = getUnavailableDates(dto);

            // There should be 7 of them!
            assertNotNull(datesFound);
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
            assertEquals(datesFound.size(), 7);

        }

        @Test
        public void falseReturned_WhenNotAllDatesAvailable() {

        }

        /**************************** METHODS COPIED FROM SERVICE CLASS ******************************/
        private ArrayList<LocalDate> getUnavailableDates(CalendarDto dto) {

            // Array containing unavailable dates within span
            ArrayList<LocalDate> datesOut = new ArrayList<>();

            // Get the ad the dto points to
            Optional<Ad> ad = adRepository.findById(dto.getAdId());

            if (ad.isPresent()) {

                // Find out when the ad was created
                LocalDate startDate = ad.get().getCreated();

                // Find out when the ad expires --> startDate + 12 months
                LocalDate expirationDate = startDate.plusMonths(12);

                // Get all dates for the ad
                Set<CalendarDate> dates = adRepository.getDatesForAd(dto.getAdId());

                // Use creation and expiration to calculate span
                for (CalendarDate date : dates) {

                    // If the date is within the specified span
                    // PS: plus/minus days because we want to include the start and end dates in our search
                    if (date.getDate().isAfter(startDate.minusDays(1))
                            && date.getDate().isBefore(expirationDate.plusDays(1))) {

                        // If the date is not available
                        if (!date.isAvailable()) {

                            // Add to return-array
                            datesOut.add(date.getDate());
                        }
                    }
                }
                // Return the array and the HttpResponse
                return datesOut;
            }

            // If ad was not present in db, the dto containing an id that does not exist
            return null;
        }
    }

}
