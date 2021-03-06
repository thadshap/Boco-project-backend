package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.user.*;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RestController()
@RequestMapping("/auth")
@Api(tags = "Authorization class to handle users logging in and verifying themselves")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityService securityService;

    @PostMapping("/facebook/signin")
    public Response facebookAuth(@RequestBody SocialLoginRequest socialLoginRequest) throws IOException {
        log.info("facebook login {}", socialLoginRequest);
        return authService.loginUserFacebook(socialLoginRequest.getId_token());
    }

    @PostMapping("/google/signin")
    public Response googleAuth(@RequestBody SocialLoginRequest socialLoginRequest)
            throws GeneralSecurityException, IOException {
        log.info("facebook login {}", socialLoginRequest);
        return authService.loginUserGoogle(socialLoginRequest);
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
            throws MessagingException, IOException {
        log.debug("[X] Call to reset password");
        return authService.resetPassword(forgotPasswordDto,
                "https://" + url.getServerName() + ":" + "8080" + url.getContextPath());
    }

    @PostMapping("/renewPassword")
    @ApiOperation(value = "Endpoint to handle the new password set by the user", response = Response.class)
    public ModelAndView renewPassword(@RequestParam("token") String token,
            @RequestBody UserRenewPasswordDto renewPasswordDto) {
        log.debug("[X] Call to renew the password");
        return authService.validatePasswordThroughToken(token, renewPasswordDto);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response createUser(@RequestBody CreateAccountDto createAccount, final HttpServletRequest url)
            throws MessagingException, IOException {
        log.debug("[X] Call to get create a user");
        return authService.createUser(createAccount, url(url));
    }

    @GetMapping("/verifyEmail")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public ModelAndView verifyRegistration(@RequestParam("token") String token) {
        log.debug("[X] Call to verify user with email");
        String result = authService.validateEmailThroughToken(token);
        String resendVerificationMail = "Send verifikasjons mail p?? nytt\n"
                + "https://localhost8080/resendVerification?" + token;
        ModelAndView view = new ModelAndView("verified");
        if (result.equalsIgnoreCase("valid email")) {
            view.addObject("txt1", "Vi er glade for at du har registrert deg hos oss");
            view.addObject("txt2", "Du er verifisert og har n?? muligheten til ?? leie.");
            view.addObject("txt3", "Hvis du har tidligere verifisert kontoen din, trenger du ikke ?? gj??re det igjen.");
            return view;
        }
        view = new ModelAndView("expired");
        view.addObject("txt1",
                "Det ser ut som at valideringstiden har utl??pt. Ikke v??r redd, vi kan sende e-post p?? nytt.");
        view.addObject("url", resendVerificationMail);
        return view;
    }

    @GetMapping("/resendVerification")
    @ApiOperation(value = "Endpoint to handle verification sendt a second time", response = Response.class)
    public Response resendVerificationMail(@RequestParam("token") String prevToken, final HttpServletRequest url)
            throws MessagingException {
        log.debug("[X] Call to resend verification mail");
        return authService.createNewToken(prevToken, url);
    }

    private String url(HttpServletRequest url) {
        return "https://" + url.getServerName() + ":" + url.getServerPort() + url.getContextPath();
    }
}
