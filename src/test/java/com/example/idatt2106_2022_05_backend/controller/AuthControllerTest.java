package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserForgotPasswordDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.ResetPasswordTokenRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.security.JWTConfig;
import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.service.email.EmailServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.internet.MimeMessage;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**

@SpringBootTest(webEnvironment = MOCK)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    private static final String URI = "/auth/";
    private static final String password = "password123";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ResetPasswordTokenRepository passwordResetTokenRepository;

    @SpyBean
    private JavaMailSender mailSender;

    @Autowired
    @InjectMocks
    private EmailServiceImpl emailService;

    // @Autowired
    // private TokenFactory tokenFactory;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JWTConfig jwtConfig;

    private User user;

    private int userId;

    private RefreshToken refreshToken;

    private String rawRefreshToken;

    private String rawAccessToken;

    CreateAccountDto createAccountDto;

    LoginDto loginDto;

    UserRenewPasswordDto forgotPasswordDto;

    UserForgotPasswordDto forgotDto;

    @BeforeEach
    void setUp() throws Exception {
        createAccountDto = CreateAccountDto.builder().email("KenRobin@mail.com").firstName("ken").lastName("robin")
                .password("passord123").matchingPassword("passord123").build();

        loginDto = LoginDto.builder().password("passord123").email("andetel@stud.ntnu.no").build();

        forgotDto = UserForgotPasswordDto.builder().email("KenRobin@mail.com").build();

        forgotPasswordDto = UserRenewPasswordDto.builder().password("ken23").confirmPassword("ken23").build();

        user = User.builder().firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no")
                .password("passord123").build();

        user.setPassword(passwordEncoder.encode(password));

        // user = userRepository.save(user);

        String loginJson = objectMapper.writeValueAsString(loginDto);

        MvcResult mvcResult = mvc
                .perform(post(URI + "/" + "login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
                .andReturn();

        rawAccessToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        userId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));
        System.out.println(rawAccessToken);
    }

    @AfterEach
    void cleanup() {
        passwordResetTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("Login with facebook or google")
    void loginWithOutsideService() {
        // TODO add test
    }

    @Test
    @DisplayName("Login")
    void login() throws Exception {

    }

    @Test
    @DisplayName("Login failed wrong credentials")
    void loginWrongCredentials() throws Exception {
        // Mockito.when(authService.login(loginDto))
        // .thenReturn(new Response("jwtToken", HttpStatus.NOT_FOUND));
        //
        // mockMvc.perform(post("/login").
        // contentType(MediaType.APPLICATION_JSON)).
        // andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Forgot password successful")
    void forgotPassword() {
        // LoginRequest loginRequest = new LoginRequest(user.getEmail(), "19newPassword");
        // String loginJson = objectMapper.writeValueAsString(loginRequest);
        // UserPasswordUpdateDto update = new UserPasswordUpdateDto(password, "19newPassword");
        //
        // UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        // mvc.perform(post("/auth/change-password/")
        // .contentType(MediaType.APPLICATION_JSON).with(user(userDetails))
        // .content(objectMapper.writeValueAsString(update))
        // .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawAccessToken))
        // .andExpect(status().isOk());
        //
        // mvc.perform(post(URI + "login")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(loginJson))
        // .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Forgot password failed")
    void passwordRenewalFailed() throws Exception {
        forgotPasswordDto = new UserRenewPasswordDto(password, "19newPassword");

        forgotDto = UserForgotPasswordDto.builder().email("andetel@stud.ntnu.no").build();
        String token = mvc.perform(post("/auth/forgotPassword").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotDto))
                .header("Authorization", "Bearer " + rawAccessToken)).andReturn().toString();

        System.out.println(token);

        mvc.perform(post("/auth/renewPassword").contentType(MediaType.APPLICATION_JSON).header("Authorization",
                "Bearer " + rawAccessToken)).andExpect(status().isBadRequest());
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

    /**
     * Test that a user can be created, but the same email cannot be used two times
     *
     * @throws Exception
     *             from post request
     *
    @Test
    public void testCreateUserTwoTimesFails() throws Exception {
        // assert user != null;
        // userRepository.save(user);
        //
        // String email = user.getEmail();
        // String password = "ValidPassword123";
        //
        // CreateAccountDto validUser = CreateAccountDto.builder()
        // .firstName(user.getFirstName())
        // .lastName(user.getLastName())
        // .email(email)
        // .password(password)
        // .matchingPassword(password)
        // .build();
        //
        // mvc.perform(post("/auth/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(validUser)))
        // .andExpect(status().isImUsed())
        // .andExpect(jsonPath("$.body").value("Verifiserings mail er sendt til mailen din !"));
    }

    @Test
    @WithMockUser(value = "spring")
    public void testGetUserByUserIdReturnsResponseIncludingFollowingCount() throws Exception {
        // User testUser = userRepository.save(userFactory.getObject());
        // testUser.addFollowing(user);
        //
        // userRepository.save(testUser);
        //
        // mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
        // .contentType(MediaType.APPLICATION_JSON).with(csrf()))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.followingCount").value(testUser.getFollowing().size()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void testGetUserByUserIdWhenCurrentUserIsFollowingAndUnauthenticatedReturnsCorrectIsCurrentUserFollowing()
            throws Exception {
        // User testUser = userRepository.save(userFactory.getObject());
        // user.addFollowing(testUser);
        // userRepository.save(user);
        //
        // mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
        // .contentType(MediaType.APPLICATION_JSON).with(csrf()))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.currentUserIsFollowing").value(false));
    }

    @Test
    @WithMockUser(value = "spring")
    public void testGetUserByUserIdWhenCurrentUserIsFollowingReturnsCorrectIsCurrentUserFollowing() throws Exception {
        // User testUser = userRepository.save(userFactory.getObject());
        // user.addFollowing(testUser);
        // userRepository.save(user);
        //
        // mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
        // .with(user(userDetails))
        // .contentType(MediaType.APPLICATION_JSON))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.currentUserIsFollowing").value(true));
    }

    /**
     * Test that a user cannot be created if email is on a wrong format
     *
     * @throws Exception
     *
    @ParameterizedTest
    @MethodSource("provideInvalidEmails")
    public void testCreateUserWithInvalidEmail(String email) throws Exception {
        // String password = "ValidPassword123";
        //
        // UserRegistrationDto invalidUser = new UserRegistrationDto(firstName, lastName, password, email, email);
        //
        // mockMvc.perform(post(URI)
        // .with(csrf())
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(invalidUser)))
        // .andExpect(status().isBadRequest())
        // .andExpect(jsonPath("$.message").value("One or more method arguments are invalid"))
        // .andExpect(jsonPath("$.data.email").exists());
    }

    /**
     * Test that a user cannot be created if password is too weak
     *
     * @throws Exception
     *
    @Test
    public void testCreateUserWithInvalidPassword() throws Exception {
        // String password = "abc123";
        //
        // UserRegistrationDto invalidUser = new UserRegistrationDto(firstName, lastName, password,
        // "test@testersen.com", email);
        //
        // mockMvc.perform(post(URI)
        // .with(csrf())
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(invalidUser)))
        // .andExpect(status().isBadRequest())
        // .andExpect(jsonPath("$.message").value("One or more method arguments are invalid"))
        // .andExpect(jsonPath("$.data.password").exists());
    }

    /**
     * Tests that get return a correct user according to token
     *
     * @throws Exception
     *
    @Test
    public void testGetUserReturnsCorrectUser() throws Exception {
        // UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        // mockMvc.perform(get(URI + "me/")
        // .with(user(userDetails)))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.id").value(user.getId().toString()));
    }

    @Test
    @DisplayName("Verify registration")
    void verifyRegistration() {
    }

    @Test
    @DisplayName("Verify registration failed")
    void verifyRegistrationFailed() {
    }
    */
