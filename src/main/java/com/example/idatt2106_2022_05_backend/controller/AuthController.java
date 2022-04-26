package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserForgotPasswordDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.registration.RegistrationComplete;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Slf4j
@RestController()
@RequestMapping("auth")
@Api(tags = "Authorization class to handle users logging in and verifying themselves")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/login/outside")
    @ApiOperation(value = "Endpoint to handle user logging in with Facebook or Google", response = Response.class)
    public Response loginWithOutsideService(Principal principal) {
        log.debug("[X] Call to login with facebook or google");
        return null;
    }

    @PostMapping("/login")
    @ApiOperation(value = "Endpoint handling user login", response = Response.class)
    public Response login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        log.debug("[X] Call to login");
        return authService.login(loginDto);
    }

    @PostMapping("/forgotPassword")
    @ApiOperation(value = "Endpoint to handle forgotten password", response = Response.class)
    public Response forgotPassword(@RequestBody UserForgotPasswordDto forgotPasswordDto, HttpServletRequest url)
            throws MessagingException {
        log.debug("[X] Call to reset password");
        return authService.resetPassword(forgotPasswordDto, url(url));
    }

    @PostMapping("/renewPassword")
    @ApiOperation(value = "Endpoint to handle the new password set by the user", response = Response.class)
    public Response renewPassword(@RequestParam("token") String token,
            @RequestBody UserRenewPasswordDto renewPasswordDto) {
        log.debug("[X] Call to renew the password");
        return authService.validatePasswordThroughToken(token, renewPasswordDto);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response createUser(@RequestBody CreateAccountDto createAccount, final HttpServletRequest url) {
        log.debug("[X] Call to get create a user");
        User user = authService.createUser(createAccount);
        if (user == null) {
            return new Response("Mail is already registered", HttpStatus.IM_USED);
        }
        publisher.publishEvent(new RegistrationComplete(user, url(url)));
        return new Response("Registration mail is created", HttpStatus.CREATED);
    }

    @GetMapping("/verifyEmail")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response verifyRegistration(@RequestParam("token") String token) {
        log.debug("[X] Call to verify user with email");
        String result = authService.validateEmailThroughToken(token);
        String resendVerificationMail = "Send verifikasjons mail på nytt\n" + "http://localhost8080/resendVerification?"
                + token;
        if (result.equalsIgnoreCase("valid email")) {
            return new Response("Kontoen er nå verifisert!\n:" + authService.getUserJWT(token), HttpStatus.ACCEPTED);
        }
        return new Response(result + "\n" + resendVerificationMail, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/resendVerification")
    @ApiOperation(value = "Endpoint to handle verification sendt a second time", response = Response.class)
    public Response resendVerificationMail(@RequestParam("token") String prevToken, final HttpServletRequest url)
            throws MessagingException {
        log.debug("[X] Call to resend verification mail");
        return authService.createNewToken(prevToken, url);
    }

    private String url(HttpServletRequest url) {
        return "http://" + url.getServerName() + ":" + url.getServerPort() + url.getContextPath();
    }
}
