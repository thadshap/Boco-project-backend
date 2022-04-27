package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserForgotPasswordDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.util.Response;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    User createUser(CreateAccountDto createAccount);

    void saveUserVerificationTokenForUser(String token, User user);

    String validateEmailThroughToken(String token);

    Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException;

    Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url) throws MessagingException;

    Response validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto);

    Response login(LoginDto loginDto) throws Exception;

    void updateAuthenticationType(String username, String database);

    String getUserJWT(String token);
}
