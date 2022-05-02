package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.OutputMessage;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    Response getAllMessagesByGroupId(long groupId);

    Response getChat(long id);

    void broadcast(Message message);

    //Response getGroupChatsBasedOnUserId(long id);

}