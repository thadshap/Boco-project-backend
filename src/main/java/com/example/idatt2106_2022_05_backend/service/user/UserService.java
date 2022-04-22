package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.util.Response;

public interface UserService {
    Response deleteUser(Long userId);

    Response updateUser(Long userId, UserUpdateDto userUpdateDto);

    Response getUser(Long userId);
}
