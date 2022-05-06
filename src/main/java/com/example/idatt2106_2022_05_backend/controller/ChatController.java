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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

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
        logger.debug("Message sent to groupId: " + groupId + " from " + messageDto.getUserId());
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
        logger.debug("Call to create a two-user group");
        return chatService.createTwoUserGroup(privateGroupDto);
    }

    @PostMapping("/create/group/list")
    @ApiOperation(value = "Endpoint to create a group from list of userId", response = Response.class)
    public Response createGroupFromUserIds(@RequestBody ListGroupDto listGroupDto) {
        logger.debug("Call to create group from multiple userIds");
        return chatService.createGroupFromUserIds(listGroupDto);
    }

    @PostMapping("/create/group/email")
    @ApiOperation(value = "Endpoint to create a group from list of emails", response = Response.class)
    public Response createGroupFromUserEmail(@RequestBody EmailListGroupDto emailListGroupDto) {
        logger.debug("Call to create group from multiple emails");
        return chatService.createGroupFromUserEmail(emailListGroupDto);
    }


    @PutMapping("/group/name/{groupId}/{newName}")
    @ApiOperation(value = "Endpoint to change group name from groupId", response = Response.class)
    public Response changeGroupNameFromGroupId(@PathVariable long groupId, @PathVariable String newName) {
        logger.debug("Call to change group name");
        return chatService.changeGroupNameFromGroupId(groupId, newName);
    }

    @GetMapping("/user/groupchat/{userId}")
    @ApiOperation(value = "Endpoint to get all groups by user id", response = Response.class)
    public Response getGroupChatSubscriptions(@PathVariable long userId) {
        logger.debug("Call to get all groups by user id");
        return chatService.getGroupChatsBasedOnUserId(userId);
    }

    @GetMapping("/group/messages/{groupId}")
    @ApiOperation(value = "Endpoint to get all messages in group by group id", response = Response.class)
    public Response getGroupMessagesByGroupId(@PathVariable long groupId) {
        logger.debug("Call to get all messages in group");
        return chatService.getAllMessagesByGroupId(groupId);
    }

    @GetMapping("/group/users/{groupId}")
    @ApiOperation(value = "Endpoint to get all users by group id", response = Response.class)
    public Response getGroupUsersByGroupId(@PathVariable long groupId) {
        logger.debug("Call to get all users by group id");
        return chatService.getGroupUsersByGroupId(groupId);
    }

    @PutMapping("/group/remove/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint remove a user from group", response = Response.class)
    public Response removeUserFromGroupById(@PathVariable long groupId, @PathVariable long userId) {
        logger.debug("Call to remove a user from group");
        return chatService.removeUserFromGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint to add user to existing group with userId", response = Response.class)
    public Response addUserToGroupById(@PathVariable long groupId, @PathVariable long userId) {
        logger.debug("Call to add user to group by id");
        return chatService.addUserToGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/email/{groupId}/{email}")
    @ApiOperation(value = "Endpoint to add user to existing group with email", response = Response.class)
    public Response addUserToGroupByEmail(@PathVariable long groupId, @PathVariable String email) {
        logger.debug("Call to add user to group by email");
        return chatService.addUserToGroupByEmail(groupId, email);
    }

}
