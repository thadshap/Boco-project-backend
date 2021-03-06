package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController()
@RequestMapping("/user")
@Api(tags = "Controller class to handle user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @PutMapping("/{userId}")
    @ApiOperation(value = "Endpoint to update user", response = Response.class)
    public Response update(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) throws IOException {
        log.debug("[X] Call to update user with id = {}", userId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return userService.updateUser(userId, userUpdateDto);
    }

    @PutMapping("/profilePicture/{userId}")
    @ApiOperation(value = "Endpoint to update user profile picture", response = Response.class)
    public Response updatePicture(@PathVariable Long userId, @RequestPart("file") MultipartFile file)
            throws IOException {
        log.debug("[X] Call to update user with id = {}", userId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return userService.updatePicture(userId, file);
    }

    @GetMapping("/profilePicture/{userId}")
    @ApiOperation(value = "Endpoint to get user profile picture", response = Response.class)
    public PictureReturnDto getPicture(@PathVariable Long userId) throws IOException {
        log.debug("[X] Call to update user with id = {}", userId);
        return userService.getPicture(userId);
    }

    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Endpoint to delete user", response = Response.class)
    public Response deleteUser(@PathVariable Long userId) {
        log.debug("[X] Call to delete user with id = {}", userId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Endpoint to get user", response = UserReturnDto.class)
    public Response getUser(@PathVariable Long userId, Authentication auth) {
        log.debug("[X] Call to get user with id = {}", userId);
        return userService.getUser(userId);
    }
}
