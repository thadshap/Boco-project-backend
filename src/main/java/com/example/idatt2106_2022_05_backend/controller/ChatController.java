package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.service.chat.ChatService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ChatController {


    @Autowired
    private ChatService chatService;

    //TODO: fix send message, save message

    @RequestMapping("/group/{id}")
    public Response onOpen(@PathVariable long id){
        return chatService.getChat(id);
    }
}
