package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.chat.GroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.service.rental.RentalService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
    UserService userService;

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
    RentalService rentalService;

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
        User user1Saved = userRepository.save(user1);
        User user2Saved = userRepository.save(user2);
        User user3Saved = userRepository.save(user3);


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
        /**
        AdDto ad = AdDto.builder().
                title("Nike shoes generic title").
                description("Renting out a pair of shoes in size 40").
                rental(true).
                rentedOut(false).
                durationType(AdType.WEEK).
                price(100).
                streetAddress("Fjordvegen 2").
                postalCode(9990).
                city("Båtsfjord").
                userId(user1.getId()).
                categoryId(it.getId()).
                build();
         */

        Ad ad = Ad.builder().
                title("Nike shoes generic title").
                description("Renting out a pair of shoes in size 40").
                rental(true).
                rentedOut(false).
                durationType(AdType.WEEK).
                price(100).
                streetAddress("Fjordvegen 2").
                postalCode(9990).
                city("Båtsfjord").
                user(user1).
                category(it).
                build();

        // Post the ad
        Ad adSaved = adRepository.save(ad);
        //adService.postNewAd(ad);

        // Retrieve the ad
        // Set<Ad> ads = adRepository.findByTitle("Nike shoes generic title");
        // Ad adFound = ads.stream().findFirst().get();

        assertNotNull(adSaved);

        // Generate a rental dto
        /**
        RentalDto dto = RentalDto.builder().
                adId(adSaved.getId()).
                owner(user1Saved.getEmail()).
                borrower(user2Saved.getEmail()).
                active(false).
                dateOfRental(LocalDate.now()).
                rentFrom(LocalDate.now().plusDays(1)).
                rentTo(LocalDate.now().plusWeeks(1)).
                deadline(LocalDate.now().plusDays(2)).build();

        // Create a rental
        rentalService.createRental(dto);
         */
        Rental rental = Rental.builder().
                ad(adSaved).
                owner(user1Saved).
                borrower(user2Saved).
                active(false).
                dateOfRental(LocalDate.now()).
                rentFrom(LocalDate.now().plusDays(1)).
                rentTo(LocalDate.now().plusWeeks(1)).
                deadline(LocalDate.now().plusDays(2)).build();
        rentalRepository.save(rental);

        // Creating a group chat
        // Creating list to hold user ids
        Set<Long> ids = new HashSet<>();
        ids.add(user1Saved.getId());
        ids.add(user2Saved.getId());
        ids.add(user3Saved.getId());

        // ListGroupDto needed to call on method
        ListGroupDto dto = new ListGroupDto();
        dto.setGroupName("Group for tests");
        dto.setUserIds(ids);

        // Creating a group chat
        chatService.createGroupFromUserIds(dto);
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
        groupRepository.deleteAll();
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

                // Get current size of group chat repo
                int sizeAtStart = groupRepository.findAll().size();

                // To perform this method a PrivateGroupDto is needed
                PrivateGroupDto dto = new PrivateGroupDto();
                dto.setGroupName("Chat");
                dto.setUserOneId(user1.getId());
                dto.setUserTwoId(user2.getId());

                // Perform the method from service
                ResponseEntity<Object> response = chatService.createTwoUserGroup(dto);
                assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

                int sizeAtEnd = groupRepository.findAll().size();

                // Assert that another group is added
                assertNotEquals(sizeAtStart, sizeAtEnd);
                assertEquals(sizeAtStart + 1, sizeAtEnd);
                // Perform the copied version from service
                GroupDto result = Methods.this.createTwoUserGroup(dto);
                assertNotNull(result);
            }

            @Test
            public void createGroupFromUserIds() {
                // Retrieve 3 users from db
                User user1 = userRepository.findAll().get(0);
                User user2 = userRepository.findAll().get(1);
                User user3 = userRepository.findAll().get(2);

                // Assert that users exist
                assertNotNull(user1);
                assertNotNull(user2);
                assertNotNull(user3);

                // Creating list to hold user ids
                Set<Long> ids = new HashSet<>();
                ids.add(user1.getId());
                ids.add(user2.getId());
                ids.add(user3.getId());

                // ListGroupDto needed to call on method
                ListGroupDto dto = new ListGroupDto();
                dto.setGroupName("Group for tests");
                dto.setUserIds(ids);

                // Testing method
                ResponseEntity<Object> response = chatService.createGroupFromUserIds(dto);
                assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

                // Testing body of method using copied over method
                GroupDto result = Methods.this.createGroupFromUserIds(dto);
                assertNotNull(result);

                // Fetch the created group
                Optional<Group> groupFound = groupRepository.findById(result.getGroupId());
                assertTrue(groupFound.isPresent());

                Group group = groupFound.get();

                // Assert that all 3 users are saved into the group-chat
                assertEquals(group.getUsers().size(),3);
            }

            @Test
            public void sendRentalMessage() {
                // Get two users
                User user1 = userRepository.findAll().get(0);
                User user2 = userRepository.findAll().get(1);

                // Assert that users exist
                assertNotNull(user1);
                assertNotNull(user2);

                // Get an ad
                Ad ad = adRepository.findAll().get(0);

                // A rental is created in the setUp --> retrieving it
                Rental rental = rentalRepository.findAll().get(0);

                // Assert that the rental was found
                assertNotNull(rental);

                assertEquals(ad.getId(), rental.getAd().getId());


                // Create a rental dto
                RentalDto dto = RentalDto.builder().
                        adId(ad.getId()).
                        rentTo(rental.getRentTo()).
                        rentFrom(rental.getRentFrom()).
                        borrower(rental.getBorrower().getEmail()).
                        owner(rental.getOwner().getEmail()).
                        price(ad.getPrice()).id(rental.getId()).
                        build();

                // Get the number of messages in db before we run the method
                int prevNumber = messageRepository.findAll().size();

                // Performing the message
                chatService.sendRentalMessage(dto);

                // Get the new number of messages in db
                int newNumber = messageRepository.findAll().size();

                // Assert that they are different
                assertNotEquals(prevNumber, newNumber);

                // Assert that this method saves a message in repo
            }

            @Test
            public void sendMessage() {
                // Retrieve a user and group
                User user = userRepository.findAll().get(0);
                Group group = groupRepository.findAll().get(0);

                // Assert not null
                assertNotNull(user);
                assertNotNull(group);

                // MessageDto needed to perform method
                MessageDto dto = new MessageDto();
                dto.setContent("A message..");
                dto.setUserId(user.getId());

                // Retrieve the old amount of messages in db
                int oldAmount = messageRepository.findAll().size();

                // Performing method
                MessageDto response = chatService.sendMessage(group.getId(), dto);

                // Retrieve the new amount of messages in db
                int newAmount = messageRepository.findAll().size();
                assertNotEquals(oldAmount, newAmount);

                // Assert that an encoding was created
                assertFalse(response.getBase64().isEmpty());
            }
        }

        @Nested
        class GetTests {
            @Test
            public void getAllMessagesByGroupId() {
                // Retrieve a user and group
                User user = userRepository.findAll().get(0);
                Group group = groupRepository.findAll().get(0);

                // Assert not null
                assertNotNull(user);
                assertNotNull(group);

                // MessageDto needed to perform method
                MessageDto dto = new MessageDto();
                dto.setContent("A message..");
                dto.setUserId(user.getId());

                // Get number of messages for the group currently
                int oldNumber = Methods.this.getAllMessagesByGroupId(group.getId()).size();
                assertTrue(oldNumber == 0);

                // Performing method to set up the test
                chatService.sendMessage(group.getId(), dto);
                chatService.sendMessage(group.getId(), dto);


                // Get the messages for the group
                ResponseEntity<Object> response = chatService.getAllMessagesByGroupId(group.getId());
                assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

                // Use copied over method in order to count the messages --> there should be 1!
                List<MessageDto> result = Methods.this.getAllMessagesByGroupId(group.getId());

                // Assert that a message was sent
                assertNotEquals(oldNumber, result.size());

                // 2 because the method was performed three times above
                assertEquals(result.size(), 2);
            }

            @Test
            public void getGroupUsersByGroupId() {
                // Retrieve a user and group
                Group group = groupRepository.findAll().get(0);

                // Assert not null
                assertNotNull(group);

                // Get the number of members
                int numberOfMembers = group.getUsers().size();

                // Perform the method
                ResponseEntity<Object> response = chatService.getGroupUsersByGroupId(group.getId());
                List<Long> ids = Methods.this.getGroupUsersByGroupId(group.getId());

                // Assert result
                assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
                assertEquals(ids.size(), numberOfMembers);
            }

            @Test
            public void getGroupChatsBasedOnUserId() {
                // Retrieve a user and group
                User user1 = userRepository.findAll().get(0);
                User user2 = userRepository.findAll().get(1);
                User user3 = userRepository.findAll().get(2);

                Group group = groupRepository.findAll().get(0);

                // Assert not null
                assertNotNull(user1);
                assertNotNull(user2);
                assertNotNull(user3);

                assertNotNull(group);

                // Get group chats based on user id
                List<GroupDto> groupsBefore = Methods.this.getGroupChatsBasedOnUserId(user1.getId());

                // Creating list to hold user ids
                Set<Long> ids = new HashSet<>();
                ids.add(user1.getId());
                ids.add(user2.getId());
                ids.add(user3.getId());

                // ListGroupDto needed to call on method
                ListGroupDto dto = new ListGroupDto();
                dto.setGroupName("New group!");
                dto.setUserIds(ids);

                // Create a new group
                Methods.this.createGroupFromUserIds(dto);

                // Get group chats based on user id
                List<GroupDto> groupsAfter = Methods.this.getGroupChatsBasedOnUserId(user1.getId());

                // Assert that new and old number of chats is not the same
                assertNotEquals(groupsBefore.size(), groupsAfter.size());

                // Use the service method
                ResponseEntity<Object> response = chatService.getGroupChatsBasedOnUserId(user1.getId());

                // Assert response
                assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            }

        }

        @Nested
        class RemovalTests {

            @Test
            public void removeUserFromGroupById() {
                // Assert that there are users and groups in db
                assertTrue(userRepository.findAll().size() > 0);
                assertTrue(groupRepository.findAll().size() > 0);

                // Get a group
                Group group = groupRepository.findAll().get(0);

                // Get the previous number of users in the group
                int prevMemberCount = group.getUsers().size();

                // Get a user from the group
                Optional<User> userFound = group.getUsers().stream().findFirst();

                // Assert that the user exists
                assertTrue(userFound.isPresent());
                User user = userFound.get();

                // Remove the user from the group
                ResponseEntity<Object> response = chatService.
                        removeUserFromGroupById(group.getId(), user.getId());

                Optional<Group> groupFound = groupRepository.findById(group.getId());

                // Assert that the group exists
                assertTrue(groupFound.isPresent());

                // Get the new number of users in the group
                int newMemberCount = groupFound.get().getUsers().size();

                // Assert proper response
                assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

                // Assert that the member count decreased
                assertNotEquals(newMemberCount, prevMemberCount);

            }
        }

        @Nested
        class UpdateTests {

            @Test
            public void changeGroupNameFromGroupId() {
                // Assert that there are users and groups in db
                assertTrue(groupRepository.findAll().size() > 0);

                // Get a group
                Group group = groupRepository.findAll().get(0);

                // Update the group with a new name!
                ResponseEntity<Object> response = chatService.
                        changeGroupNameFromGroupId(group.getId(), "A new name :-)");

                // Assert response code
                assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

                // Assert that the previous and new name is different
                String prevName = group.getName();
                String newName = groupRepository.findById(group.getId()).get().getName();

                assertNotEquals(prevName, newName);
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

        public List<Long> getGroupUsersByGroupId(long groupId) {
            Group group = getGroup(groupId);

            List<User> users = new ArrayList<>(group.getUsers());
            List<Long> userIds = new ArrayList<>();

            for (int i = 0; i < users.size(); i++) {
                userIds.add(users.get(i).getId());
            }

            return userIds;
        }

        public List<GroupDto> getGroupChatsBasedOnUserId(long id) {
            User user = getUser(id);

            Set<Group> groups = user.getGroupChats();
            List<Group> groupsL = new ArrayList<>(groups);
            List<GroupDto> grps = new ArrayList<>();

            for (int i = 0; i < groupsL.size(); i++) {
                grps.add(new GroupDto(groupsL.get(i).getId(), groupsL.get(i).getName()));
            }

            return grps;
        }

        public List<MessageDto> getAllMessagesByGroupId(long groupId) {
            Group group = getGroup(groupId);

            Set<Message> messages = messageRepository.findAllByGroup(group);
            List<Message> msL = new ArrayList<>(messages);
            List<MessageDto> messageDtoList = new ArrayList<>();

            for (int i = 0; i < msL.size(); i++) {
                Message ms = msL.get(i);

                PictureReturnDto pRDto = userService.getPicture(ms.getUser().getId());
                //TODO change when getPicture changes
                if(pRDto == null){
                    pRDto = new PictureReturnDto(0L,"no picture", "no picture");
                }

                String ts = ms.getTimestamp().toString().split("\\.")[0];
                MessageDto messageDto = new MessageDto(
                        ms.getContent(),
                        ts, ms.getUser().getId(),
                        ms.getUser().getFirstName(),
                        ms.getUser().getLastName(),
                        pRDto.getType(),
                        pRDto.getBase64());
                messageDtoList.add(messageDto);
            }

            return messageDtoList;
        }

        private Group getGroup(long id) {
            return groupRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke gruppechat"));
        }

        public GroupDto createGroupFromUserIds(ListGroupDto listGroupDto) {
            //TODO multiple of same user given?
            List<Long> userIds = new ArrayList<>(listGroupDto.getUserIds());
            Set<User> users = new HashSet<>();

            for (int i = 0; i < userIds.size(); i++) {
                users.add(getUser(userIds.get(i)));
            }

            if (users.size() == 2) {
                Group group = checkIfUsersHavePrivateGroup(users);
                if (group != null) {
                    return new GroupDto(group.getId(),group.getName());
                }
            }

            Group newGroup = Group.builder()
                    .name(listGroupDto.getGroupName())
                    .users(users)
                    .build();
            groupRepository.save(newGroup);

            GroupDto groupDto = new GroupDto();
            groupDto.setGroupId(newGroup.getId());
            groupDto.setGroupName(newGroup.getName());

            return groupDto;
        }

        private User getUser(long userId) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke brukeren"));
        }

        private Group checkIfUsersHavePrivateGroup(Set<User> usr) {
            List<User> users = new ArrayList<>(usr);
            User userOne = users.get(0);
            User userTwo = users.get(1);

            Set<Group> grps = userOne.getGroupChats();
            List<Group> groups = new ArrayList<>(grps);

            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                if (group.getUsers().size() == 2) {
                    if (group.getUsers().contains(userTwo)) {
                        return group;
                    }
                }
            }

            return null;
        }

    }

}
