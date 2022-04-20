package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.model.UserVerificationToken;
import com.example.idatt2106_2022_05_backend.util.Response;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    User createUser(CreateAccountDto createAccount);

    void saveUserVerificationTokenForUser(String token, User user);

    String validateEmailThroughToken(String token);

    Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException;

}
