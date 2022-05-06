package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.chat.EmailListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:8080")
@Api(tags = "Controller class to handle group chats, messages and websocket subscribing.")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    SecurityService securityService;

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/messages/{groupId}")
    public MessageDto sendMessage(@DestinationVariable Long groupId, MessageDto messageDto) {
        log.debug("[X] Call to send message to group with id = {}", groupId);
        MessageDto msgDto = chatService.sendMessage(groupId, messageDto);
        if (msgDto == null) {
            logger.debug("Not sending message to subscribers because user is not in group.");
            return null;
        }
        return msgDto;
    }

    @PostMapping("/create/group")
    @ApiOperation(value = "Endpoint to create a two-user group", response = Response.class)
    public Response createTwoUserGroup(@RequestBody PrivateGroupDto privateGroupDto) {
        log.debug("[X] Call to create a two user group with name = {}", privateGroupDto.getGroupName());
        if (!(securityService.isUser(privateGroupDto.getUserOneId()) || securityService.isUser(privateGroupDto.getUserOneId()))) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return chatService.createTwoUserGroup(privateGroupDto);
    }

    @PostMapping("/create/group/list")
    @ApiOperation(value = "Endpoint to create a group from list of userId", response = Response.class)
    public Response createGroupFromUserIds(@RequestBody ListGroupDto listGroupDto) {
        log.debug("[X] Call to create a group with multiple userIds with name = {}", listGroupDto.getGroupName());
        return chatService.createGroupFromUserIds(listGroupDto);
    }

    @PostMapping("/create/group/email")
    @ApiOperation(value = "Endpoint to create a group from list of emails", response = Response.class)
    public Response createGroupFromUserEmail(@RequestBody EmailListGroupDto emailListGroupDto) {
        log.debug("[X] Call to create a group with multiple emails with name = {}", emailListGroupDto.getGroupName());
        return chatService.createGroupFromUserEmail(emailListGroupDto);
    }


    @PutMapping("/group/name/{groupId}/{newName}")
    @ApiOperation(value = "Endpoint to change group name from groupId", response = Response.class)
    public Response changeGroupNameFromGroupId(@PathVariable long groupId, @PathVariable String newName) {
        log.debug("[X] Call to change group name with id = {}", groupId);
        return chatService.changeGroupNameFromGroupId(groupId, newName);
    }

    @GetMapping("/user/groupchat/{userId}")
    @ApiOperation(value = "Endpoint to get all groups by user id", response = Response.class)
    public Response getGroupChatSubscriptions(@PathVariable long userId) {
        log.debug("[X] Call to get all groups user is in with id = {}", userId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return chatService.getGroupChatsBasedOnUserId(userId);
    }

    @GetMapping("/group/messages/{groupId}")
    @ApiOperation(value = "Endpoint to get all messages in group by group id", response = Response.class)
    public Response getGroupMessagesByGroupId(@PathVariable long groupId) {
        log.debug("[X] Call to get messages from group with id = {}", groupId);
        return chatService.getAllMessagesByGroupId(groupId);
    }

    @GetMapping("/group/users/{groupId}")
    @ApiOperation(value = "Endpoint to get all users by group id", response = Response.class)
    public Response getGroupUsersByGroupId(@PathVariable long groupId) {
        log.debug("[X] Call to get all users in group with id = {}", groupId);
        return chatService.getGroupUsersByGroupId(groupId);
    }

    @PutMapping("/group/remove/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint remove a user from group", response = Response.class)
    public Response removeUserFromGroupById(@PathVariable long groupId, @PathVariable long userId) {
        log.debug("[X] Call to remove user with id = " + userId + " from group with id = " + groupId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang", HttpStatus.BAD_REQUEST);
        }
        return chatService.removeUserFromGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint to add user to existing group with userId", response = Response.class)
    public Response addUserToGroupById(@PathVariable long groupId, @PathVariable long userId) {
        log.debug("[X] Call to add user with id = " + userId + " to group with id = " + groupId);
        return chatService.addUserToGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/email/{groupId}/{email}")
    @ApiOperation(value = "Endpoint to add user to existing group with email", response = Response.class)
    public Response addUserToGroupByEmail(@PathVariable long groupId, @PathVariable String email) {
        log.debug("[X] Call to add user with email = " + email + " to group with id = " + groupId);
        return chatService.addUserToGroupByEmail(groupId, email);
    }

}
