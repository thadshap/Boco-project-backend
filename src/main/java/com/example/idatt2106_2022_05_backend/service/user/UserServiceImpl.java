package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.user.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Service class to handle user objects
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PictureUtility pictureService;

    private ModelMapper modelMapper = new ModelMapper();

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public Response updateUser(Long userId, UserUpdateDto userUpdateDto) throws IOException {
        Optional<User> userFromDB = userRepository.findById(userId);
        if(userFromDB.isEmpty()){
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userFromDB.get();
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
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        if(!userUpdateDto.getPicture().isEmpty()) {
            pictureService.savePicture(userUpdateDto.getPicture(),0,userId);
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
        Optional<User> userFromDB = userRepository.findById(userId);
        if (userFromDB.isEmpty()) {
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        UserReturnDto user = modelMapper.map(userFromDB.get(), UserReturnDto.class);
        return new Response(user, HttpStatus.OK);
    }

}
