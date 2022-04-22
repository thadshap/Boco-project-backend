package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.util.Response;

import java.io.IOException;

public interface UserService {
    Response deleteUser(Long userId);

    Response updateUser(Long userId, UserUpdateDto userUpdateDto) throws IOException;

    Response getUser(Long userId);
}
