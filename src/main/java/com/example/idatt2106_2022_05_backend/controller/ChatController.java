package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.WebSocket;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:8081")
public class ChatController {

    @Autowired
    private ChatService chatService;

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    //TODO: fix send message, save message
    //TODO: subcribe on groupchat, get path
/*
    @GetMapping("/user/groupchat/{userId}")
    public Response getGroupChatSubscriptions(@PathVariable long id){
        return chatService.getGroupChatsBasedOnUserId(id);
    }
*/

    @MessageMapping("/topic")
    public MessageDto sendMessage(@PathVariable long groupId, @Payload MessageDto messageDto ) {
        logger.info("got to controller");
        //headerAccessor.getSessionAttributes().put("userid", messageDto.getFromUserId());
        //headerAccessor.getSessionAttributes().put("groupId", groupId);
        chatService.saveMessage(messageDto, groupId);
        return messageDto;
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

}
