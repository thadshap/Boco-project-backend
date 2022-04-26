package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface ChatService {

    Response getAllMessagesByGroupId(long groupId);

    Response saveMessage(MessageDto message);

}
