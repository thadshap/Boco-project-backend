package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.chat.*;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Date;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    private Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    // private support method
    private Group getGroup(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke gruppechat"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke brukeren"));
    }

    /**
     *
     * Helping method to check if two users have a two-user group with each other.
     *
     * @param usrs
     *            List of two users to check
     * 
     * @return returns group if a group is found, null if no group is found
     */
    private Group checkIfUsersHavePrivateGroup(Set<User> usrs, String name) {
        List<User> users = new ArrayList<>(usrs);
        User userOne = users.get(0);
        User userTwo = users.get(1);

        Set<Group> grps = userOne.getGroupChats();
        List<Group> groups = new ArrayList<>(grps);

        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            if (group.getUsers().size() == 2) {
                if (group.getUsers().contains(userTwo) && group.getName().equals(name)) {
                    return group;
                }
            }
        }

        return null;
    };

    /**
     *
     * Method to send a rental message from borrower to owner.
     *
     * @param rentalDto
     *            {@link RentalDto} object with information to update a rental
     *
     */
    @Override
    public void sendRentalMessage(RentalDto rentalDto) {
        User owner = userRepository.findByEmail(rentalDto.getOwner());
        User borrower = userRepository.findByEmail(rentalDto.getBorrower());
        Set<User> users = new HashSet<>();
        users.add(owner);
        users.add(borrower);

        Ad ad = adRepository.findById(rentalDto.getAdId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke ad"));

        Group group = checkIfUsersHavePrivateGroup(users, ad.getTitle());

        if (group == null) {
            group = Group.builder().name(ad.getTitle()).users(users).build();
        }

        String content = "Hei, " + owner.getFirstName() + " " + owner.getLastName() + "! Jeg vil leie fra annonsen "
                + ad.getTitle() + ".\n" + "Fra: " + rentalDto.getRentFrom() + " til " + rentalDto.getRentTo() + "\n"
                + "Pris: " + rentalDto.getPrice() + "kr \n" + "https://localhost:8080/approve_rental?rentalId="
                + rentalDto.getId();

        Message message = new Message();
        message.setContent(content);
        message.setUser(borrower);
        message.setGroup(group);
        message.setTimestamp(Timestamp.from(Instant.now()));

        groupRepository.save(group);
        messageRepository.save(message);
    }

    /**
     *
     * Method to get all messages in a group-
     *
     * @param groupId
     *            id of group to get messages from
     *
     * @return returns HttpStatus and list of messages.
     */
    @Override
    public Response getAllMessagesByGroupId(long groupId) {
        Group group = getGroup(groupId);

        Set<Message> messages = messageRepository.findAllByGroup(group);
        List<Message> msL = new ArrayList<>(messages);
        List<MessageDto> messageDtoList = new ArrayList<>();

        for (int i = 0; i < msL.size(); i++) {
            Message ms = msL.get(i);

            PictureReturnDto pRDto = userService.getPicture(ms.getUser().getId());

            if (pRDto == null) {
                pRDto = new PictureReturnDto(0L, "no picture", "no picture");
            }

            String ts = ms.getTimestamp().toString().split("\\.")[0];
            MessageDto messageDto = new MessageDto(ms.getContent(), ts, ms.getUser().getId(),
                    ms.getUser().getFirstName(), ms.getUser().getLastName(), pRDto.getType(), pRDto.getBase64());
            messageDtoList.add(messageDto);
        }

        return new Response(messageDtoList, HttpStatus.OK);
    }

    /**
     *
     * Method to get all userIds in a group
     *
     * @param groupId
     *            id of group to get users from
     *
     * @return returns HttpStatus and a list of userIds.
     */
    @Override
    public Response getGroupUsersByGroupId(long groupId) {
        Group group = getGroup(groupId);

        List<User> users = new ArrayList<>(group.getUsers());
        List<Long> userIds = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            userIds.add(users.get(i).getId());
        }

        return new Response(userIds, HttpStatus.OK);
    }

    /**
     *
     * Method to create a new group with two users.
     *
     * @param privateGroupDto
     *            {@link PrivateGroupDto} object with information to create a two user group.
     *
     * @return returns HttpStatus and created group.
     */
    @Override
    public Response createTwoUserGroup(PrivateGroupDto privateGroupDto) {
        if (privateGroupDto.getUserOneId() == privateGroupDto.getUserTwoId()) {
            return new Response("Users must be different, same userId given.", HttpStatus.BAD_REQUEST);
        }

        Group newGroup = new Group();
        newGroup.setName(privateGroupDto.getGroupName());

        Optional<User> userOneFound = userRepository.findById(privateGroupDto.getUserOneId());
        Optional<User> userTwoFound = userRepository.findById(privateGroupDto.getUserTwoId());

        if (userOneFound.isPresent() && userTwoFound.isPresent()) {
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

            return new Response(groupDto, HttpStatus.OK);
        } else {
            return new Response("Could not find one or both of the users", HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * Method to create group from multiple userIds.
     *
     * @param listGroupDto
     *            {@link ListGroupDto} object with information to create a multiple user group.
     *
     * @return returns HttpStatus and created group.
     */
    @Override
    public Response createGroupFromUserIds(ListGroupDto listGroupDto) {
        List<Long> userIds = new ArrayList<>(listGroupDto.getUserIds());
        Set<User> users = new HashSet<>();

        for (int i = 0; i < userIds.size(); i++) {
            users.add(getUser(userIds.get(i)));
        }

        if (users.size() == 2) {
            Group group = checkIfUsersHavePrivateGroup(users, listGroupDto.getGroupName());
            if (group != null) {
                return new Response(new GroupDto(group.getId(), group.getName()), HttpStatus.OK);
            }
        }

        Group newGroup = Group.builder().name(listGroupDto.getGroupName()).users(users).build();
        groupRepository.save(newGroup);

        GroupDto groupDto = new GroupDto();
        groupDto.setGroupId(newGroup.getId());
        groupDto.setGroupName(newGroup.getName());

        return new Response(groupDto, HttpStatus.OK);
    }

    /**
     *
     * Method to create a new group from a list of emails.
     *
     * @param emailListGroupDto
     *            {@link EmailListGroupDto} object with information needed to create a multiple user group.
     *
     * @return returns HttpStatus and created group.
     */
    @Override
    public Response createGroupFromUserEmail(EmailListGroupDto emailListGroupDto) {
        List<String> emails = new ArrayList<>(emailListGroupDto.getEmails());
        Set<User> users = new HashSet<>();
        Set<String> failedEmails = new HashSet<>();

        for (int i = 0; i < emails.size(); i++) {
            User user = userRepository.findByEmail(emails.get(i));
            if (user == null) {
                logger.debug("Did not find user with email: " + emails.get(i));
                failedEmails.add(emails.get(i));
            } else {
                users.add(userRepository.findByEmail(emails.get(i)));
            }
        }
        if (users.size() < 2) {
            return new Response(EmailListGroupReturnDto.builder().succeeded(false).failedEmails(failedEmails)
                    .groupName(null).groupId(null).build(), HttpStatus.OK);
        }

        if (users.size() == 2) {
            Group group = checkIfUsersHavePrivateGroup(users, emailListGroupDto.getGroupName());
            if (group != null) {
                return new Response(EmailListGroupReturnDto.builder().succeeded(true).failedEmails(failedEmails)
                        .groupName(group.getName()).groupId(group.getId()).build(), HttpStatus.OK);
            }
        }

        Group newGroup = Group.builder().name(emailListGroupDto.getGroupName()).users(users).build();
        groupRepository.save(newGroup);

        GroupDto groupDto = new GroupDto();
        groupDto.setGroupId(newGroup.getId());
        groupDto.setGroupName(newGroup.getName());

        return new Response(EmailListGroupReturnDto.builder().succeeded(true).failedEmails(failedEmails)
                .groupName(newGroup.getName()).groupId(newGroup.getId()).build(), HttpStatus.OK);
    }

    /**
     *
     * Method to change group name.
     *
     * @param groupId
     *            id of group to change name.
     * @param newName
     *            new name of group.
     *
     * @return returns HttpStatus.
     */
    @Override
    public Response changeGroupNameFromGroupId(long groupId, String newName) {
        if (newName == null || newName.isEmpty() || newName.trim().isEmpty()) {
            return new Response("New name given is empty", HttpStatus.BAD_REQUEST);
        }
        Group group = getGroup(groupId);
        group.setName(newName);
        groupRepository.save(group);
        return new Response("Group name changed", HttpStatus.OK);
    }

    /**
     *
     * Method to store incoming message in repository, and ready message to be sent to other users.
     *
     * @param groupId
     *            group id of message to be sant.
     * @param messageDto
     *            {@link MessageDto} object with all message data needed by chat.
     *
     * @return return messageDto to send to other users
     */
    @Override
    public MessageDto sendMessage(Long groupId, MessageDto messageDto) {
        User user = getUser(messageDto.getUserId());
        Group group = getGroup(groupId);

        if (!group.getUsers().contains(user)) {
            logger.info("Sender of message, Id: " + user.getId() + ", was not in group, not sending message to group.");
            return null;
        }

        Timestamp ts = Timestamp.from(Instant.now());
        Message message = Message.builder().timestamp(ts).content(messageDto.getContent()).group(group).user(user)
                .build();

        messageRepository.save(message);

        PictureReturnDto pRDto = userService.getPicture(messageDto.getUserId());

        if (pRDto == null) {
            pRDto = new PictureReturnDto(0L, "no picture", "no picture");
        }

        messageDto.setFirstName(user.getFirstName());
        messageDto.setLastName(user.getLastName());
        messageDto.setTimeStamp(ts.toString().split("\\.")[0]);
        messageDto.setType(pRDto.getType());
        messageDto.setBase64(pRDto.getBase64());

        return messageDto;
    }

    /**
     *
     * Method to get group chats that a user is in.
     *
     * @param id
     *            user id to get groups of.
     *
     * @return return HttpStatus and list og GroupDto objects.
     */
    public Response getGroupChatsBasedOnUserId(long id) {
        User user = getUser(id);

        Set<Group> groups = user.getGroupChats();
        List<Group> groupsL = new ArrayList<>(groups);
        List<GroupDto> grps = new ArrayList<>();

        for (int i = 0; i < groupsL.size(); i++) {
            grps.add(new GroupDto(groupsL.get(i).getId(), groupsL.get(i).getName()));
        }

        return new Response(grps, HttpStatus.OK);
    }

    /**
     *
     * Method to remove a user from group.
     *
     * @param groupId
     *            group id to remove user from.
     * @param userId
     *            user id to be removed from group.
     *
     * @return return HttpStatus with result message.
     */
    @Override
    public Response removeUserFromGroupById(long groupId, long userId) {
        Group group = getGroup(groupId);
        User user = getUser(userId);
        Set<User> users = group.getUsers();

        if (users.contains(user)) {
            users.remove(user);
            group.setUsers(users);
            groupRepository.save(group);

            if (group.getUsers().size() == 0) {
                groupRepository.delete(group);
            }
            return new Response("User removed", HttpStatus.OK);
        }

        return new Response("User not found in group", HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * Method to add a user to group.
     *
     * @param groupId
     *            group id to add user to.
     * @param userId
     *            user id to add to group.
     *
     * @return return HttpStatus and result message.
     */
    @Override
    public Response addUserToGroupById(long groupId, long userId) {
        Group group = getGroup(groupId);
        User user = getUser(userId);
        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is already in group", HttpStatus.NOT_FOUND);
        }

        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);

        return new Response("User added to group", HttpStatus.OK);
    }

    /**
     *
     * Method to add user to group by email.
     *
     * @param groupId
     *            group id to add user to.
     * @param email
     *            email of user to add to group.
     *
     * @return return HttpStatus
     */
    @Override
    public Response addUserToGroupByEmail(long groupId, String email) {
        Group group = getGroup(groupId);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new Response("Could not find user with email: " + email, HttpStatus.NOT_FOUND);
        }

        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is already in group", HttpStatus.NOT_FOUND);
        }

        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);

        return new Response("User added to group", HttpStatus.OK);
    }

}
