package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.stream.Stream;


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
public class UserControllerTest {
	
	private String URI = "/user";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JWTUtil jwtConfig;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	
	private String firstName;
	
	private String lastName;
	
	private String email;

	private String password;

	private UserDetails userDetails;

	/**
	 * Setting up variables that is the same for all tests
	 */
	@BeforeEach
	public void setUp() throws Exception {
		user = User.builder()
				.firstName("Anders")
				.lastName("Tellefsen")
				.email("andetel@stud.ntnu.no")
				.password("passord123")
				.build();
		assert user != null;
		userRepository.save(user);
		firstName = "Test";
		lastName = "Testersen";
		email = "test@testersen.no";
		password = "passord123";
	}
	
	/**
	 * Cleans up the saved users after each test
	 */
	@AfterEach
	public void cleanUp(){
//		userRepository.deleteAll();
	}

	/**
	 * Provides a stream of Valid emails to provide parameterized test
	 *
	 * @return Stream of valid emails
	 */
	private static Stream<Arguments> provideValidEmails() {
		return Stream.of(
				Arguments.of("test123@mail.com"),
				Arguments.of("test1.testesen@mail.com"),
				Arguments.of("test_1234-testesen@mail.com")
		);
	}
	
	/**
	 * Provides a stream of Invalid emails to provide parameterized test
	 *
	 * @return Stream of invalid emails
	 */
	private static Stream<Arguments> provideInvalidEmails() {
		return Stream.of(
				Arguments.of("test123.no"),
				Arguments.of("test@"),
				Arguments.of("test@mail..com")
		);
	}
	
	/**
	 * Test that you cannot update a user with invalid input but get ok back
	 *
	 * @throws Exception from post request
	 */
	@ParameterizedTest
	@MethodSource("provideInvalidEmails")
	public void testUpdateUserWithInValidEmailButConnect(String email) throws Exception {
		
//		CreateAccountDto validUser = CreateAccountDto.builder()
//				.firstName(firstName)
//				.lastName(lastName)
//				.email(email)
//				.password(password)
//				.matchingPassword(password)
//				.build();
//
//		mockMvc.perform(put(URI+"/1")
//				.with(csrf())
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(validUser)))
//				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserId() throws Exception {

		User testUser = userRepository.save(user);
		mockMvc.perform(get(URI +"/"+ testUser.getId().toString())
				.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(testUser.getFirstName()));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdReturnsUserNotVerified() throws Exception {
		User testUser = userRepository.save(user);
		mockMvc.perform(get(URI + "/" + testUser.getId().toString())
								.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verified").value(false));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdReturnsResponseIncludingFollowerCount() throws Exception {
		User testUser = userRepository.save(user);
		user.setVerified(true);

		userRepository.save(user);

		mockMvc.perform(get(URI + "/" + testUser.getId().toString())
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verified").value(true));
	}
	
	/**
	 * Tests that put updated a user and returns the updated user info
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserUpdatesUserAndReturnUpdatedData() throws Exception {
//		String lastName = "ThisIsARandomString";
//		user.setLastName(lastName);
//		UserUpdateDto userUpdateDto = UserUpdateDto.builder()
//				.firstName("")
//				.lastName(lastName)
//				.password("")
//				.email("")
//				.build();
//
//		mockMvc.perform(put(URI + "/" + user.getId())
//				.contentType(MediaType.APPLICATION_JSON)
//						.with(csrf())
//						.content(objectMapper.writeValueAsString(userUpdateDto)))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.body").value("User updated"));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testDeleteUserAndReturnsOk() throws Exception {
//		User userToDelete = user;
//		assert userToDelete != null;
//		userToDelete = userRepository.save(userToDelete);
//
//		UserDetails userDetails = UserDetailsImpl.builder().email(userToDelete.getEmail()).build();

//		mockMvc.perform(delete(URI + "/1")
//				.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isAccepted())
//				.andExpect(jsonPath("$.message").value("User deleted"));
	}

	
}
