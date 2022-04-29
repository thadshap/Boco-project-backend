package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.OutputMessage;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

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

    @MessageMapping("/chat")
    //@SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        logger.info("got to controller");
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        //headerAccessor.getSessionAttributes().put("userid", messageDto.getFromUserId());
        //headerAccessor.getSessionAttributes().put("groupId", groupId);
        chatService.saveMessage(message, groupId);
        return new OutputMessage(message.getPayload());
    }




    @RequestMapping("/group/{id}")
    public Response onOpen(@PathVariable long id){
        return chatService.getChat(id);
    }
}
