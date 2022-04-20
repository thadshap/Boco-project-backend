package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.LoginDto;
import com.example.idatt2106_2022_05_backend.service.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController("/auth")
@Api(tags = "Authorization class to handle users logging in ")
public class AuthController {

    @Autowired
    private UserService userService;


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
    @ApiOperation(value = "Endpoint to handle renewal of password", response = Response.class)
    public Response forgotPassword(){

    }

    @PostMapping("/create")
    @ApiOperation(value = "", response = Response.class)
    public Response createUser(){

    }
}
