package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.Message;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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

    @Override
    public void sendMessageGroup(long groupId, MessageDto message) {
        //save message to database

        simpMessagingTemplate.convertAndSend("/topic/group/" + groupId, message);
    }

}
