package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/user")
@Api(tags = "Controller class to handle user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{userId}")
    @ApiOperation(value = "Endpoint to update user", response = Response.class)
    public Response update(@RequestParam Long userId, @RequestBody UserUpdateDto userUpdateDto){
        return userService.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Endpoint to delete user", response = Response.class)
    public Response deleteUser(@RequestParam Long userId){
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Endpoint to delete user", response = Response.class)
    public Response getUser(@RequestParam Long userId){
        return userService.getUser(userId);
    }
}
