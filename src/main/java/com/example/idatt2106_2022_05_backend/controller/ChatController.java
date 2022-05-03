package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:8080")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    SecurityService securityService;

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    //TODO: fix send message, save message
    //TODO: subcribe on groupchat, get path
/*
    @GetMapping("/user/groupchat/{userId}")
    public Response getGroupChatSubscriptions(@PathVariable long id){
        return chatService.getGroupChatsBasedOnUserId(id);
    }
*/

    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/messages/{groupId}")
    public MessageDto sendMessage(@DestinationVariable Long groupId, MessageDto messageDto) {
        logger.info("Message sent to groupId: " + groupId);
        MessageDto msgDto = chatService.sendMessage(groupId, messageDto);

        return msgDto;
    }


    @RequestMapping("/group/{id}")
    public Response onOpen(@PathVariable long id){
        return chatService.getChat(id);
    }

    @PostMapping("/create/group")
    @ApiOperation(value = "Endpoint to create a two-user group", response = Response.class)
    public Response createTwoUserGroup(@RequestBody PrivateGroupDto privateGroupDto) {
        logger.info("Call to create a two-user group");
        return chatService.createTwoUserGroup(privateGroupDto);
    }

    @PostMapping("/create/group/list")
    @ApiOperation(value = "Endpoint to create a group from list of userId", response = Response.class)
    public Response createGroupFromUserIds(@RequestBody ListGroupDto listGroupDto){
        return chatService.createGroupFromUserIds(listGroupDto);
    }
    @PutMapping("/group/name/{groupId}/{newName}")
    @ApiOperation(value = "Endpoint to change group name from groupId", response = Response.class)
    public Response changeGroupNameFromGroupId(@PathVariable long groupId, @PathVariable String newName) {
        return chatService.changeGroupNameFromGroupId(groupId, newName);
    }

    @GetMapping("/user/groupchat/{userId}")
    @ApiOperation(value = "Endpoint to get all groups by user id", response = Response.class)
    public Response getGroupChatSubscriptions(@PathVariable long userId){
        return chatService.getGroupChatsBasedOnUserId(userId);
    }

    @GetMapping("/group/messages/{groupId}")
    @ApiOperation(value = "Endpoint to get all messages by group id", response = Response.class)
    public Response getGroupMessagesByGroupId(@PathVariable long groupId) {
        return chatService.getAllMessagesByGroupId(groupId);
    }

    @PutMapping("/group/remove/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint remove a user from group", response = Response.class)
    public Response removeUserFromGroupById(@PathVariable long groupId, @PathVariable long userId) {
        return chatService.removeUserFromGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/{groupId}/{userId}")
    @ApiOperation(value = "Endpoint to add user to existing group with userId", response = Response.class)
    public Response addUserToGroupById(@PathVariable long groupId, @PathVariable long userId) {
        return chatService.addUserToGroupById(groupId, userId);
    }

    @PutMapping("/group/add/user/email/{groupId}/{email}")
    @ApiOperation(value = "Endpoint to add user to existing group with email", response = Response.class)
    public Response addUserToGroupByEmail(@PathVariable long groupId, @PathVariable String email) {
        return chatService.addUserToGroupByEmail(groupId, email);
    }

}
