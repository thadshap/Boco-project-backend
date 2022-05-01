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
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController()
@RequestMapping("/auth")
@Api(tags = "Authorization class to handle users logging in and verifying themselves")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityService securityService;

    @GetMapping(value = "/signin/facebook")
    @ApiOperation(value = "Endpoint to handle user logging in with Facebook")
    public String signinFacebook() {
        log.debug("[X] Call to login with facebook");
        String url = authService.getFacebookUrl();
        log.info("Url: " + url);
        return url;
    }

    @GetMapping(value = "/forwardLogin/facebook")
    @ApiOperation(value = "Endpoint to handle user logging in with Facebook", response = ModelAndView.class)
    public RedirectView forwardFacebook(@RequestParam("code") String authorizationCode) {
//        log.info(principal.toString());
        log.debug("[X] Call to forward login with facebook to facebook");
        return authService.forwardToFacebook(authorizationCode);
    }

    @GetMapping(value = "/signin/google")
    @ApiOperation(value = "Endpoint to handle user logging in with Google")
    public String signinGoogle() {
        log.debug("[X] Call to login with google");
        String url = authService.getGoogleUrl();
        System.out.println("The URL is: " + url);
        return url;
    }

    @RequestMapping(value = "/forwardLogin/google")
    @ApiOperation(value = "Endpoint to handle user logging in with Google", response = ModelAndView.class)
    public RedirectView forwardGoogle(@RequestParam("code") String authorizationCode) {
        log.debug("[X] Call to forward login with facebook to Google");
        return authService.forwardToGoogle(authorizationCode);
    }

    @PostMapping("/facebook/signin")
    public Response facebookAuth(@Valid @RequestBody SocialLoginRequest facebookLoginRequest) {
        log.info("facebook login {}", facebookLoginRequest);
        return authService.loginUserFacebook(facebookLoginRequest.getAccessToken());
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
        return authService.resetPassword(forgotPasswordDto, "http://" + url.getServerName() + ":" + "8080" + url.getContextPath());
    }

    @PostMapping("/renewPassword")
    @ApiOperation(value = "Endpoint to handle the new password set by the user", response = Response.class)
    public ModelAndView renewPassword(@RequestParam("token") String token,
            @RequestBody UserRenewPasswordDto renewPasswordDto) {
        System.out.println(renewPasswordDto.getPassword() + " " + renewPasswordDto.getConfirmPassword());
        System.out.println(token);
        log.debug("[X] Call to renew the password");
        return authService.validatePasswordThroughToken(token, renewPasswordDto);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response createUser(@RequestBody CreateAccountDto createAccount, final HttpServletRequest url) {
        log.debug("[X] Call to get create a user");
        return authService.createUser(createAccount, url(url));
    }

    @GetMapping("/verifyEmail")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public ModelAndView verifyRegistration(@RequestParam("token") String token) {
        log.debug("[X] Call to verify user with email");
        String result = authService.validateEmailThroughToken(token);
        String resendVerificationMail = "Send verifikasjons mail på nytt\n" + "http://localhost8080/resendVerification?"
                + token;
        ModelAndView view = new ModelAndView("verified");
        if (result.equalsIgnoreCase("valid email")) {
            view.addObject("txt1", "Vi er glade for at du har registrert deg hos oss");
            view.addObject("txt2", "Du er verifisert og har nå muligheten til å leie.");
            view.addObject("txt3", "Hvis du har tidligere verifisert kontoen din, trenger du ikke å gjøre det igjen.");
            return view;
        }
        view.addObject("txt1", "Tidsfristen for å endre passord er gått ut!!!");
        view.addObject("txt2", "Du kan fortsatt verifisere deg med å sende ett nytt verifikasjonsmail.");
        view.addObject("txt3", resendVerificationMail);
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
        return "http://" + url.getServerName() + ":" + url.getServerPort() + url.getContextPath();
    }
}
