package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Set;

@RestController
@RequestMapping("/chat")
public class ChatController {

    //@Autowired
    //private WebSocket webSocket;

    @Autowired
    private ChatService chatService;

    //ikke stomp?
    /**
    @RequestMapping("/")
    public Response onMessage(Message message){

    }

    @RequestMapping("/{id}")
    public Response onOpen(@PathParam("id") long id){
        return chatService.getAllMessagesByGroupId(id);
    }
    **/

    //STOMP,
    @MessageMapping("/group/{groupId}")
    public void sendMessage(MessageDto message) {
        chatService.saveMessage(message);

    }

    @GetMapping("group/messages/{groupId}")
    public Response getAllMessagesByGroupId(@PathVariable("groupId") long groupId) {
        return chatService.getAllMessagesByGroupId(groupId);
    }
}
