package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private final String url = "http://localhost:8080/auth";

    private CreateAccountDto createAccountDto;

    private LoginDto loginDto;

    private UserRenewPasswordDto forgotPasswordDto;

    @BeforeEach
    void setUp() {
        createAccountDto = CreateAccountDto.builder()
                .email("KenRobin@mail.com")
                .firstName("ken")
                .lastName("robin")
                .password("passord123")
                .matchingPassword("passord123")
                .build();

        loginDto = LoginDto.builder()
                .password("passord123")
                .email("KenRobin@mail.com")
                .build();

        forgotPasswordDto = UserRenewPasswordDto.builder()
//                .email("KenRobin@mail.com")
                .password("ken23")
                .confirmPassword("ken23")
                .build();
    }

    @Test
    @DisplayName("")
    void loginWithOutsideService() {
        //TODO add test
    }

    @Test
    @DisplayName("Login")
    void login() throws Exception {
//        Mockito.when(authService.createUser(createAccountDto))
//                .thenReturn(new User());
//
//
//        Mockito.when(authService.login(loginDto))
//                .thenReturn(new Response("jwtToken", HttpStatus.ACCEPTED));
//
//        mockMvc.perform(post("/login").
//                        contentType(MediaType.APPLICATION_JSON)).
//                andExpect(status().isAccepted()).
//                andExpect(jsonPath("$.title").value("jwtToken"));
    }

    @Test
    @DisplayName("Login failed wrong credentials")
    void loginWrongCredentials() throws Exception {
//        Mockito.when(authService.login(loginDto))
//                .thenReturn(new Response("jwtToken", HttpStatus.NOT_FOUND));
//
//        mockMvc.perform(post("/login").
//                        contentType(MediaType.APPLICATION_JSON)).
//                andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Forgot password successful")
    void forgotPassword() {

    }

    @Test
    @DisplayName("Forgot password failed")
    void forgotPasswordFailed() throws Exception {
//        Mockito.when(authService.login(loginDto))
//                .thenReturn(new Response("jwtToken", HttpStatus.NOT_FOUND));
//
//        mockMvc.perform(post("/login").
//                        contentType(MediaType.APPLICATION_JSON)).
//                andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Renew password")
    void renewPassword() {
    }

    @Test
    @DisplayName("Renew password failed")
    void renewPasswordFailed() {
    }

    @Test
    @DisplayName("Create user")
    void createUser() {

    }

    @Test
    @DisplayName("Create user failed")
    void createUserFailed() {

    }

    @Test
    @DisplayName("Verify registration")
    void verifyRegistration() {
    }

    @Test
    @DisplayName("Verify registration failed")
    void verifyRegistrationFailed() {
    }
}