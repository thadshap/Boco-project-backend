package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.util.Response;

import java.io.IOException;

import java.io.IOException;
import java.util.Optional;

public interface UserService {

    Response deleteUser(Long userId);

    Response deleteProfilePicture(long userId, byte[] chosenPicture);

    Response updateUser(Long userId, UserUpdateDto userUpdateDto) throws IOException;

    Response getUser(Long userId);

}
