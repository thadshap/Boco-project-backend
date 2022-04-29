package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

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

    @MessageMapping("/app")
    @SendTo("/topic/messages")
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
}
