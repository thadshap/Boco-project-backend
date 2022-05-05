package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.chat.GroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ChatIntegrationTest {

    @Autowired
    AdService adService;
    @Autowired
    AdRepository adRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChatService chatService;


    @Autowired
    ReviewRepository reviewRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        // Building 3 users
        User user1 = User.builder().firstName("firstName1").lastName("lastName1").email("user1.name1@hotmail.com")
                .password("pass1word").build();
        User user2 = User.builder().firstName("firstName2").lastName("lastName2").email("user2.name2@hotmail.com")
                .password("pass1word").build();
        User user3 = User.builder().firstName("firstName3").lastName("lastName3").email("user3.name3@hotmail.com")
                .password("pass1word").build();


        // Saving the users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);


        // Building categories
        Category clothes = Category.builder().
                name("new category1").
                parent(true).
                build();

        Category it = Category.builder().
                name("new category2").
                parent(true).
                build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);

        // Building an ad-dto with foreign keys
        AdDto ad = AdDto.builder().
                title("Nike shoes").
                description("Renting out a pair of shoes in size 40").
                rental(true).
                rentedOut(false).
                durationType(AdType.WEEK).
                price(100).
                streetAddress("Project Road 4").
                postalCode(7234).
                userId(user1.getId()).
                categoryId(it.getId()).
                build();

            // Post the ad
            adService.postNewAd(ad);
    }

    @AfterEach
    public void emptyDatabase() {
        reviewRepository.deleteAll();
        rentalRepository.deleteAll();
        pictureRepository.deleteAll();
        messageRepository.deleteAll();
        userRepository.deleteAll();
        adRepository.deleteAll();
        // messageRepository.deleteAll();
        // outputMessageRepository.deleteAll();
        // userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    class Methods {

        @Nested
        protected class CreateTests {

            // Creates a group using only two users
            @Test
            public void createTwoUserGroup() {
                // Retrieve 2 users from db
                User user1 = userRepository.findAll().get(0);
                User user2 = userRepository.findAll().get(1);

                // Assert that the users exist
                assertNotNull(user1);
                assertNotNull(user2);

                // To perform this method a PrivateGroupDto is needed
                PrivateGroupDto dto = new PrivateGroupDto();
                dto.setGroupName("Chat");
                dto.setUserOneId(user1.getId());
                dto.setUserTwoId(user2.getId());

                // Perform the method from service
                ResponseEntity<Object> response = chatService.createTwoUserGroup(dto);
                assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

                // Perform the copied version from service
                GroupDto result = Methods.this.createTwoUserGroup(dto);
                assertNotNull(result);
            }

            @Test
            public void createGroupFromUserIds() {

            }

            @Test
            public void sendRentalMessage() {
                // Assert that this method saves a message in repo
            }

            @Test
            public void sendMessage() {

            }
        }

        @Nested
        class GetTests {
            @Test
            public void getAllMessagesByGroupId() {

            }

            @Test
            public void getGroupUsersByGroupId() {

            }

            @Test
            public void getGroupChatsBasedOnUserId() {

            }

        }

        @Nested
        class RemovalTests {
            @Test
            public void removeUserFromGroupById() {

            }
        }

        @Nested
        class UpdateTests {
            @Test
            public void changeGroupNameFromGroupId() {

            }
        }

        @Nested
        class addTests {

            @Test
            public void addUserToGroupById() {

            }

            @Test
            public void addUserToGroupByEmail() {

            }
        }
        /************************ METHODS COPIED IN FROM CHAT-SERVICE (FOR TESTING) ***********************/
        public GroupDto createTwoUserGroup(PrivateGroupDto privateGroupDto) {
            //TODO check if group with the users already exists
            if (privateGroupDto.getUserOneId() == privateGroupDto.getUserTwoId()) {
                return null;
            }

            Group newGroup = new Group();
            newGroup.setName(privateGroupDto.getGroupName());

            Optional<User> userOneFound = userRepository.findById(privateGroupDto.getUserOneId());
            Optional<User> userTwoFound = userRepository.findById(privateGroupDto.getUserTwoId());

            if(userOneFound.isPresent() && userTwoFound.isPresent()) {
                // Retrieve the users
                User userOne = userOneFound.get();
                User userTwo = userTwoFound.get();

                // Generate a hash-set to hold the users
                HashSet<User> users = new HashSet<>();
                users.add(userOne);
                users.add(userTwo);
                newGroup.setUsers(users);

                groupRepository.save(newGroup);

                GroupDto groupDto = new GroupDto();
                groupDto.setGroupId(newGroup.getId());
                groupDto.setGroupName(newGroup.getName());

                return groupDto;
            }
            else {
                return null;
            }
        }
    }

}
