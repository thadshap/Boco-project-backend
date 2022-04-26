package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ChatServiceImpl implements ChatService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    GroupRepository groupRepository;

    @Override
    public Set<Message> getAllMessagesByGroupId(long groupId){
           return messageRepository.getMessagesByGroupId(groupId);
    }

    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
