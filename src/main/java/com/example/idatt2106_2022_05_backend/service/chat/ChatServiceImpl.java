package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.chat.GroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
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

    //private support method
    private Group getGroup(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke gruppechat"));
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
    };

    /**
     *
     * Method to send a rental message from borrower to owner.
     *
     * @param rentalDto
     *      {@link RentalDto} object with information to update a rental
     *
     */
    @Override
    public void sendRentalMessage(RentalDto rentalDto){
        User owner = userRepository.findByEmail(rentalDto.getOwner());
        User borrower = userRepository.findByEmail(rentalDto.getBorrower());
        Set<User> users = new HashSet<>();
        users.add(owner);
        users.add(borrower);

        Group group = checkIfUsersHavePrivateGroup(users);

        if (group == null){
            logger.debug("New Group being created for rental message.");
            group = Group.builder()
                    .name("NAME")
                    .users(users)
                    .build();
            groupRepository.save(group);
        }
        Ad ad = adRepository.findById(rentalDto.getAdId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Fant ikke ad"));

        String content = "Hei, jeg vil leie fra annonsen " + ad.getTitle() + ".\n" +
                "Fra: " + rentalDto.getRentFrom() + " til " + rentalDto.getRentTo() + "\n" +
                "Pris: " + rentalDto.getPrice() + "kr \n" +
                "http://localhost:8080/rental/approve_rental/?rentalId=" + rentalDto.getId();

        Message message = new Message();
        message.setContent(content);
        message.setUser(borrower);
        message.setGroup(group);
        message.setTimestamp(Timestamp.from(Instant.now()));

        messageRepository.save(message);
    }

    /**
     *
     * Method to get all messages in a group-
     *
     * @param groupId id of group to get messages from
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

        return new Response(messageDtoList, HttpStatus.OK);
    }

    /**
     *
     * Method to get all userIds in a group
     *
     * @param groupId id of group to get users from
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

    @Override
    public void broadcast(MessageDto message) {
        logger.info("Go to service");
        Message outputMessage = new Message();

        //String text = new String((byte[])message.getPayload(), StandardCharsets.UTF_8);
        outputMessage.setContent(message.getContent());

        //TODO: Add user check and set in outputmessage
        //outputMessage.setFrom();

        //String messageDestination = destination.get(0).split(" ")[0];
        //String path = "topic/messages/"+messageDestination;

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        //outputMessage.setTimestamp(time);
        //ouputMessageRepository.save(outputMessage);
        //String path = "topic/messages/"+ message.getGroupId();
        //logger.info("Sending "+message.getContent() + " to :" + path);
        //simpMessagingTemplate.convertAndSend(path, message);

    }

    /**
     *
     * Method to create a new group with two users.
     *
     * @param privateGroupDto
     *      {@link PrivateGroupDto} object with information to create a two user group.
     *
     * @return returns HttpStatus and created group.
     */
    @Override
    public Response createTwoUserGroup(PrivateGroupDto privateGroupDto) {
        //TODO check if group with the users already exists
        if (privateGroupDto.getUserOneId() == privateGroupDto.getUserTwoId()) {
            return new Response("Users must be different, same userId given.", HttpStatus.BAD_REQUEST);
        }

        Group newGroup = new Group();
        newGroup.setName(privateGroupDto.getGroupName());

        User userOne = userRepository.getById(privateGroupDto.getUserOneId());
        User userTwo = userRepository.getById(privateGroupDto.getUserTwoId());

        HashSet<User> users = new HashSet<>();
        users.add(userOne);
        users.add(userTwo);
        newGroup.setUsers(users);

        groupRepository.save(newGroup);

        GroupDto groupDto = new GroupDto();
        groupDto.setGroupId(newGroup.getId());
        groupDto.setGroupName(newGroup.getName());

        return new Response(groupDto, HttpStatus.OK);
    }

    /**
     *
     * Method to create group from multiple userIds.
     *
     * @param listGroupDto {@link ListGroupDto} object with information to create a multiple user group.
     *
     * @return returns HttpStatus and created group.
     */
    @Override
    public Response createGroupFromUserIds(ListGroupDto listGroupDto) {
        //TODO multiple of same user given?
        List<Long> userIds = new ArrayList<>(listGroupDto.getUserIds());
        Set<User> users = new HashSet<>();

        for (int i = 0; i < userIds.size(); i++) {
            users.add(getUser(userIds.get(i)));
        }

        if (users.size() == 2) {
            Group group = checkIfUsersHavePrivateGroup(users);
            if (group != null) {
                return new Response(new GroupDto(group.getId(),group.getName()), HttpStatus.OK);
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

        return new Response(groupDto, HttpStatus.OK);
    }

    /**
     *
     * Method to change group name.
     *
     * @param groupId id of group to change name.
     * @param newName new name of group.
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
     * @param groupId group id of message to be sant.
     * @param messageDto {@link MessageDto} object with all message data needed by chat.
     *
     * @return return messageDto to send to other users
     */
    @Override
    public MessageDto sendMessage(Long groupId, MessageDto messageDto) {
        User user = getUser(messageDto.getUserId());
        Group group = getGroup(groupId);
        Timestamp ts = Timestamp.from(Instant.now());
        Message message = Message.builder()
                .timestamp(ts)
                .content(messageDto.getContent())
                .group(group)
                .user(user)
                .build();

        messageRepository.save(message);

        PictureReturnDto pRDto = userService.getPicture(messageDto.getUserId());
        //TODO change when getPicture changes
        if(pRDto == null){
            pRDto = new PictureReturnDto(0L,"no picture", "no picture");
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
     * @param id user id to get groups of.
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
     * @param groupId group id to remove user from.
     * @param userId user id to be removed from group.
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
            return new Response("User removed", HttpStatus.OK);
        }

        return new Response("User not found in group", HttpStatus.BAD_REQUEST);

    }

    /**
     *
     * Method to add a user to group.
     *
     * @param groupId group id to add user to.
     * @param userId user id to add to group.
     *
     * @return return HttpStatus and result message.
     */
    @Override
    public Response addUserToGroupById(long groupId, long userId) {
        Group group = getGroup(groupId);
        User user = getUser(userId);
        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is allready in group", HttpStatus.NOT_FOUND);
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
     * @param groupId group id to add user to.
     * @param email email of user to add to group.
     *
     * @return return HttpStatus
     */
    @Override
    public Response addUserToGroupByEmail(long groupId, String email) {
        Group group = getGroup(groupId);
        User user = userRepository.findByEmail(email);

        if(user == null) {
            return new Response("Could not find user with email: " + email, HttpStatus.NOT_FOUND);
        }

        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is allready in group", HttpStatus.NOT_FOUND);
        }

        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);

        return new Response("User added to group", HttpStatus.OK);
    }

}
