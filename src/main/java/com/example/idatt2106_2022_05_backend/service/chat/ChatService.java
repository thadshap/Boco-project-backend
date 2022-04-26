package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface ChatService {
    Set<Message> getAllMessagesByGroupId(long groupId);

    void saveMessage(Message message);
}
