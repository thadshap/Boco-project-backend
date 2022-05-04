package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.chat.GroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    /*
     * @Override public Response getAllMessagesByGroupId(long groupId){ Group group = getGroup(groupId);
     * List<MessageDto> messageDtoList = messageRepository.findAllByGroup(group).stream() .map(message ->
     * modelMapper.map(message, MessageDto.class)) .collect(Collectors.toList());
     *
     * return new Response(messageDtoList, HttpStatus.OK); }
     */

    @Override
    public Response getAllMessagesByGroupId(long groupId) {
        Group group = getGroup(groupId);

        Set<com.example.idatt2106_2022_05_backend.model.Message> messages = messageRepository.findAllByGroup(group);
        List<com.example.idatt2106_2022_05_backend.model.Message> msL = new ArrayList<>(messages);
        List<MessageDto> messageDtoList = new ArrayList<>();

        for (int i = 0; i < msL.size(); i++) {
            Message ms = msL.get(i);
            String ts = ms.getTimestamp().toString().split("\\.")[0];
            MessageDto messageDto = new MessageDto(ms.getContent(), ts, ms.getUser().getId(),
                    ms.getUser().getFirstName(), ms.getUser().getLastName());
            messageDtoList.add(messageDto);
        }

        return new Response(messageDtoList, HttpStatus.OK);
    }

    @Override
    public void broadcast(MessageDto message) {
        logger.info("Go to service");
//        OutputMessage outputMessage = new OutputMessage();

        //String text = new String((byte[])message.getPayload(), StandardCharsets.UTF_8);
//        outputMessage.setText(message.getContent());

        //TODO: Add user check and set in outputmessage
        //outputMessage.setFrom();

        //String messageDestination = destination.get(0).split(" ")[0];
        //String path = "topic/messages/"+messageDestination;

        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        outputMessage.setTime(time);
        //ouputMessageRepository.save(outputMessage);
        //String path = "topic/messages/"+ message.getGroupId();
        //logger.info("Sending "+message.getContent() + " to :" + path);
        //simpMessagingTemplate.convertAndSend(path, message);

    }

    @Override
    public Response getChat(long id) {
//        Group group = getGroup(id);
//        List<OutputMessage> messageDtos = ouputMessageRepository.findAllByGroup(group);
//        messageDtos.sort(Comparator.comparing(OutputMessage::getTime));
//        return new Response(messageDtos.stream()
//                .map(message -> modelMapper.map(message, MessageDto.class))
//                .collect(Collectors.toList()), HttpStatus.OK);
        return null;
    }

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

        messageDto.setFirstName(user.getFirstName());
        messageDto.setLastName(user.getLastName());
        messageDto.setTimeStamp(ts.toString().split("\\.")[0]);

        return messageDto;
    }

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

    @Override
    public Response addUserToGroupById(long groupId, long userId) {
        Group group = getGroup(groupId);
        User user = getUser(userId);
        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is allready in group", HttpStatus.NOT_FOUND); //TODO occupied place
        }

        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);

        return new Response("User added to group", HttpStatus.OK);
    }

    @Override
    public Response addUserToGroupByEmail(long groupId, String email) {
        Group group = getGroup(groupId);
        User user = userRepository.findByEmail(email);

        if(user == null) {
            return new Response("Could not find user with email: " + email, HttpStatus.NO_CONTENT);
        }

        Set<User> users = group.getUsers();

        if (group.getUsers().contains(user)) {
            return new Response("User is allready in group", HttpStatus.NOT_FOUND);//TODO occupied already
        }

        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);

        return new Response("User added to group", HttpStatus.OK);
    }
/*
    public Response getGroupChatsBasedOnUserId(long id){
        User user = getUser(id);
        Set<Group> groups = groupRepository.findAllByUser(user);
        List<Long> groupId = groups.stream().map(Group::getId).collect(Collectors.toList());
        return new Response(groupId, HttpStatus.OK);
    }
*/
    /**
     * 1. Metode til å sende melding
     * 3. Metode til å hente en chat
     * TODO: paginate og sorter chat
     * 4. Get all groupchats on user
     */
}
