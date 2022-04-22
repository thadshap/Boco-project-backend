package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class to handle user objects
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();

    /**
     * Method to delete user from repository.
     * 
     * @param userId
     *            user id to delete user.
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response deleteUser(Long userId) {
        userRepository.deleteById(userId);

        return new Response("User deleted", HttpStatus.ACCEPTED);
    }

    /**
     * Method to update User object in the repository.
     * 
     * @param userId
     *            id of the user to update.
     * @param userUpdateDto
     *            {@link UserUpdateDto} object with variables to update user.
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.getById(userId);
        if (!userUpdateDto.getFirstName().isBlank()) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if (!userUpdateDto.getLastName().isBlank()) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if (!userUpdateDto.getEmail().isBlank()) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (!userUpdateDto.getPassword().isBlank()) {
            user.setPassword(userUpdateDto.getPassword());
        }
        if (userUpdateDto.getPicture() != null) {
            Picture picture = Picture.builder().filename("PB").content(userUpdateDto.getPicture()).build();
            user.setPicture(picture);
        }
        userRepository.save(user);
        return new Response("User updated", HttpStatus.OK);
    }

    /**
     * Method to retrieve user from the database.
     * 
     * @param userId
     *            id of user to retrieve.
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getUser(Long userId) {
        UserReturnDto user = modelMapper.map(userRepository.getById(userId), UserReturnDto.class);
        if (user == null) {
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        return new Response(user, HttpStatus.OK);
    }

}
