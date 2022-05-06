package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserIntegrationTest {

    @Autowired
    AdService adService;

    @Autowired
    AdRepository adRepository;

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    MessageRepository messageRepository;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        // Building a user
        User user = User.builder().firstName("firstName").lastName("lastName").email("user11.name@hotmail.com")
                .password("pass1word").build();

        User user2 = User.builder().firstName("firstName").lastName("lastName").email("user22.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        userRepository.save(user);
        userRepository.save(user2);

        // Building categories
        Category clothes = Category.builder().
                name("new category1").
                parent(true).
                build();

        Category it = Category.builder().
                name("new category2").
                parent(true).
                build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);

        // Building an ad-dto with foreign keys
         AdDto dto = AdDto.builder().
         title("unique title for this tesst1.").
         description("Renting out a pair of shoes in size 40").
         rental(true).
         rentedOut(false).
         durationType(AdType.WEEK).
         price(100).
         lat(63.401920).
         lng(10.443579).
         streetAddress("Fjordvegen 2").
         postalCode(9990).
         city("Båtsfjord").
         userId(user.getId()).
         categoryId(it.getId()).
         build();

         adService.postNewAd(dto);

         Set<Ad> adFound = adRepository.findByTitle("unique title for this tesst1.");
         Ad ad = adFound.stream().findFirst().get();

         // Create rental
        Rental rental = Rental.builder().
                ad(ad).
                owner(user).
                borrower(user2).
                active(false).
                dateOfRental(LocalDate.now()).
                rentFrom(LocalDate.now().plusDays(1)).
                rentTo(LocalDate.now().plusWeeks(1)).
                deadline(LocalDate.now().plusDays(2)).build();
        rentalRepository.save(rental);
    }

    @AfterEach
    public void emptyDatabase() {
        reviewRepository.deleteAll();
        rentalRepository.deleteAll();
        pictureRepository.deleteAll();
        messageRepository.deleteAll();
        userRepository.deleteAll();
        adRepository.deleteAll();
        // messageRepository.deleteAll();
        // outputMessageRepository.deleteAll();
        // userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    class TestUserRepo {



        @Test
        public void getUserByEmail_WhenEmailCorrect() {

            // Create new user
            User user = User.builder().firstName("firstName").lastName("lastName").email("karoline.wahl2@hotmail.com")
                    .password("pass1word").build();

            // Saving the user
            User userSaved = userRepository.save(user);

            // User must exist in order to continue
            assertNotNull(userSaved);

            String correctEmail = userSaved.getEmail();

            // Get a user by email
            User userFound = userRepository.findByEmail(correctEmail);

            // The user should be found
            assertNotNull(userFound);
            assertEquals(userFound.getEmail(), correctEmail);
        }

        @Test
        public void cannotGetUserByEmail_WhenIdWrong() {
            // Users exist in db because of dataloader
            assertNotNull(userRepository.findAll());

            // Retrieve a user and their email
            User user = userRepository.findAll().get(0);
            String wrongEmail = "wrong.email@user.com";

            // Get a user by email
            User userFound = userRepository.findByEmail(wrongEmail);

            // The dummy-data user should be existing
            assertNotNull(user);

            // The found user should be null
            assertNull(userFound);
        }

        @Test
        public void getAdsByUser_WhenIdCorrect() {
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            assertNotNull(user);

            // Building an ad with foreign keys and add it to the user
            Ad newAd = Ad.builder().title("Sail boat").description("Renting out a huge sail boat").rental(true)
                    .rentedOut(false).durationType(AdType.MONTH).price(100).streetAddress("The sea")
                    .postalCode(7000).user(user).category(category).build();

            // Persist the ad
            Ad savedAd = adRepository.save(newAd);

            // Assert that the ad was persisted
            Optional<Ad> adFound = adRepository.findById(savedAd.getId());
            assertTrue(adFound.isPresent());

            // Set foreign key both ways
            user.setAd(newAd);

            // Persist the changes
            userRepository.save(user);

            // Retrieve the ads for the user
            Set<Ad> ads = userRepository.getAdsByUserId(user.getId());

            // The list should contain an ad
            assertTrue(ads.size() > 0);
        }

        @Test
        public void cannotGetAdsByUser_WhenIdWrong() {
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            assertNotNull(user);

            // Building an ad with foreign keys and add it to the user
            Ad newAd = Ad.builder().title("Sail boat").description("Renting out a huge sail boat").rental(true)
                    .rentedOut(false).durationType(AdType.MONTH).price(100).streetAddress("The sea")
                    .postalCode(7000).user(user).category(category).build();

            // Persist the ad
            Ad savedAd = adRepository.save(newAd);

            // Assert that the ad was persisted
            Optional<Ad> adFound = adRepository.findById(savedAd.getId());
            assertTrue(adFound.isPresent());

            // Set foreign key both ways
            user.setAd(newAd);

            // Persist the changes
            userRepository.save(user);

            // Retrieve the ads for the NON-existent user
            Set<Ad> ads = userRepository.getAdsByUserId(100000L);

            // The list should be empty
            assertTrue(ads.isEmpty());
        }
    }

    @Nested
    class UpdateUserTests {

        // Update user correctly and receive OK
        @Test
        public void userIsUpdated_WhenDtoNotEmpty() {
            // Retrieve a user
            User user = userRepository.findAll().get(0);

            // This user should be existing
            assertNotNull(user);

            // Build a dto
            UserUpdateDto dto = UserUpdateDto.builder().firstName("new name").build();

            // Update the user by using the dto
            try {
                // Extract a response entity using UserService
                ResponseEntity<Object> res = userService.updateUser(user.getId(), dto);

                // Assert that the response code is OK
                assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());

            } catch (IOException e) {
                // Method fails if this exception is caught
                fail();
            }
        }

        // Update user wrongly all fields empty and no change to object with equals check
        @Test
        public void userNotUpdated_WhenDtoIsEmpty() {
            // Retrieve a user
            User user = userRepository.findAll().get(0);

            // This user should be existing
            assertNotNull(user);

            // Build an empty dto
            UserUpdateDto dto = UserUpdateDto.builder().build();

            // Update the user by using the dto
            try {
                // Extract a response entity using UserService
                ResponseEntity<Object> res = userService.updateUser(user.getId(), dto);

                // Assert that the response is null (returned when there is no attribute in dto)
                assertEquals(res, null);

                // Assert that the updated user and the previous user is the same
                assertEquals(user, userRepository.findById(user.getId()).get());

            } catch (IOException e) {
                // Method fails if this exception is caught
                fail();
            }
        }

        // Update user wrongly (user id does not exist) and receive NOT found
        @Test
        public void userNotUpdated_WhenUserIdNotCorrect() {

            // Build a dto
            UserUpdateDto dto = UserUpdateDto.builder().firstName("new name").build();

            // Create a random id that does not exist in db
            long wrongUserId = 1000000L;

            // Update the user by using the dto
            try {
                // Extract a response entity using UserService
                ResponseEntity<Object> res = userService.updateUser(wrongUserId, dto);

                // Assert that the response code is NOT_FOUND
                assertEquals(res.getStatusCodeValue(), HttpStatus.NO_CONTENT.value());

            } catch (IOException e) {
                // Method fails if this exception is caught
                fail();
            }
        }
    }

    @Nested
    class GetUserTests {

        @Test
        public void getUserByUserId_WhenIdCorrect() {
            // Existing user
            User user = userRepository.findAll().get(0);

            assertNotNull(user);

            ResponseEntity<Object> res = userService.getUser(user.getId());

            assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
        }

        @Test
        public void cannotGetUserByUserId_WhenIdWrong() {
            // Existing user
            long wrongUserId = 10000L;

            Optional<User> userFound = userRepository.findById(wrongUserId);

            // There should not exist a user with this id
            assertFalse(userFound.isPresent());

            ResponseEntity<Object> res = userService.getUser(wrongUserId);

            // HTTP response should be NOT_FOUND
            assertEquals(res.getStatusCodeValue(), HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    class DeleteUserTests {

        @Test
        public void userDeleted_WhenIdCorrect() {
            // TODO FIX THIS AFTERWARDS...
            User user = userRepository.findAll().get(1);

            ResponseEntity<Object> response = userService.deleteUser(user.getId());

            // Assert that the method passed
            assertEquals(response.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

            // Assert that the user does not exist in db
            Optional<User> userFound = userRepository.findById(user.getId());
            assertFalse(userFound.isPresent());
        }

        @Test
        public void userNotDeleted_WhenIdWrong() {
            Long wrongUserId = 100000L;

            try {
                ResponseEntity<Object> response = userService.deleteUser(wrongUserId);

                // Assert that the method passed but received a status code == NOT_FOUND
                assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
            } catch (EmptyResultDataAccessException | NoSuchElementException e) {
                // Passing the test
            }
        }
    }
}
