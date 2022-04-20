package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.LoginDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.model.UserVerificationToken;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.registration.RegistrationComplete;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController("/auth")
@Api(tags = "Authorization class to handle users logging in ")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/login/outside/service")
    @ApiOperation(value = "Endpoint to handle user logging in with Facebook or Google", response = Response.class)
    public Response loginWithOutsideService(Principal prinsipal){
        return null;
    }

    @PostMapping("/login")
    @ApiOperation(value = "Endpoint handling user login", response = Response.class)
    public Response login(@RequestParam LoginDto loginDto){

        return null;
    }

    @PostMapping("/forgotPassword")
    @ApiOperation(value = "Endpoint to handle forgotten password", response = Response.class)
    public Response forgotPassword(){
        return null;
    }

    @PostMapping("/resetPassword")
    @ApiOperation(value = "Endpoint to handle renewal of password", response = Response.class)
    public Response resetPassword(){
        return null;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response createUser(@RequestBody CreateAccountDto createAccount, final HttpServletRequest url){
        User user = authService.createUser(createAccount);
        publisher.publishEvent(new RegistrationComplete(
                user,
                url(url)
        ));
        return null;
    }

    @GetMapping("/verifyEmail")
    @ApiOperation(value = "Endpoint where user can create an account", response = Response.class)
    public Response verifyRegistration(@RequestParam("token") String token) {
        String result = authService.validateEmailThroughToken(token);
        String resendVerificationMail = "Send verifikasjons mail på nytt\n" + "http://localhost8080/resendVerification?" + token;

        if(result.equalsIgnoreCase("valid email")) {
            return new Response("Kontoen er nå verifisert!", HttpStatus.ACCEPTED);
        }
        return new Response(result + "\n" + resendVerificationMail, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/resendVerification")
    @ApiOperation(value = "", response = Response.class)
    public Response resendVerificationMail(@RequestParam("token") String prevToken, final HttpServletRequest url){
        Response response = authService.createNewToken(prevToken, url);
        return response;
    }

    private String url(HttpServletRequest url){
        return "http://" +
                url.getServerName() +
                ":" +
                url.getServerPort() +
                url.getContextPath();
    }
}
