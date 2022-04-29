package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    Response getAllMessagesByGroupId(long groupId);

    MessageDto saveMessage(MessageDto message, long groupId);

    Response getChat(long id);

    //Response getGroupChatsBasedOnUserId(long id);

}
