package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthIntegrationTests {

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
    AuthService authService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Nested
    class CreateUserTests {

        @Test
        public void userSaved_WhenCorrectInput() {
            CreateAccountDto newUser = CreateAccountDto.builder().
                    firstName("new user").
                    lastName("last name").
                    email("e.mail@hotmail.com").
                    password("Tulling").
                    matchingPassword("Tulling").
                    build();

            // Save the user
//            User result = authService.createUser(newUser);

            // Assert that the user exists in db
//            Optional<User> userFound = userRepository.findById(result.getId());
//            assertTrue(userFound.isPresent());
        }

        @Test
        public void userNotSaved_WhenWrongInput() {
            // Missing the matching password attribute
            CreateAccountDto newUser = CreateAccountDto.builder().
                    firstName("new user").
                    lastName("last name").
                    email("e.mail@hotmail.com").
                    password("Tulling").
                    build();

            // Save the user
//            User result = authService.createUser(newUser);

            // Assert that the user returned is null
//            assertNull(result);
        }
    }

    @Nested
    class TokenCreationTests {

        @Test
        public void tokenCreatedSuccessful_WhenCorrectInput() {

        }

        @Test
        public void tokenCreationFails_WhenWrongInput() {

        }
    }

    @Nested
    class LoginAuthenticationTests {

        @Test
        public void loginSuccessful_whenAuthenticated() {

        }

        @Test
        public void loginFailed_whenNotAuthenticated() {

        }
    }

    @Nested
    class PasswordResetTests {

        @Test
        public void passwordResetSuccessful_WhenCorrectInput() {

        }

        @Test
        public void passwordResetFails_WhenWrongInput() {

        }
    }

}
