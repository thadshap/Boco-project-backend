package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Response deleteUser(Long userId) {
        userRepository.deleteById(userId);

        return new Response("User deleted", HttpStatus.ACCEPTED);
    }

    @Override
    public Response updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.getById(userId);
        if(!userUpdateDto.getFirstName().isBlank()){
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if(!userUpdateDto.getLastName().isBlank()){
            user.setLastName(userUpdateDto.getLastName());
        }
        if(!userUpdateDto.getEmail().isBlank()){
            user.setEmail(userUpdateDto.getEmail());
        }
        if(!userUpdateDto.getPassword().isBlank()){
            user.setPassword(userUpdateDto.getPassword());
        }
        if(userUpdateDto.getPicture() != null){
            Picture picture = Picture.builder().filename("PB").content(userUpdateDto.getPicture()).build();
            user.setPicture(picture);
        }
        userRepository.save(user);
        return new Response("User updated", HttpStatus.OK);
    }

    @Override
    public Response getUser(Long userId) {
        User user = userRepository.getById(userId);
        if(user == null){
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        return new Response(user, HttpStatus.OK);
    }


}
