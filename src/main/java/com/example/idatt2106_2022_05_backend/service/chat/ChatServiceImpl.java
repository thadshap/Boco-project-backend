package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.MessageObjectModel;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private ModelMapper modelMapper = new ModelMapper();

    //private support method
    private Group getGroup(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke gruppechat"));
    }

    private User getUser(long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke brukeren"));
    }


    @Override
    public Response getAllMessagesByGroupId(long groupId){
        Group group = getGroup(groupId);
            List<MessageDto> messageDtoList = messageRepository.findAllByGroup(group).stream()
                    .map(message -> modelMapper.map(message, MessageDto.class))
                    .collect(Collectors.toList());
        return new Response(messageDtoList, HttpStatus.OK);
    }

    @Override
    public MessageDto saveMessage(MessageDto message, long groupId) {
        MessageObjectModel message1 = new MessageObjectModel();

        if(message.getContent().length()>280){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingen er for lang");
        }
        message1.setContent(message.getContent());
        message1.setUser(getUser(message.getFromUserId()));
        message1.setGroup(getGroup(groupId));

        //setting timestamp
        Timestamp current = Timestamp.from(Instant.now());
        message1.setTimestamp(current);

        messageRepository.save(message1);

        this.simpMessagingTemplate.convertAndSend("/topic" + message1.getGroup().getId(), message1);

        return message;
    }

    @Override
    public Response getChat(long id){
        Group group = getGroup(id);
        List<MessageObjectModel> messageDtos = messageRepository.findAllByGroup(group).stream().collect(Collectors.toList());
        messageDtos.sort(Comparator.comparing(MessageObjectModel::getTimestamp));
        return new Response(messageDtos.stream()
                .map(message -> modelMapper.map(message, MessageDto.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
/*
    public Response getGroupChatsBasedOnUserId(long id){
        User user = getUser(id);
        Set<Group> groups = groupRepository.findAllByUser(user);
        List<Long> groupId = groups.stream().map(Group::getId).collect(Collectors.toList());
        return new Response(groupId, HttpStatus.OK);
    }
*/
        /**
         * 1. Metode til å sende melding
         * 3. Metode til å hente en chat
         * TODO: paginate og sorter chat
         * 4. Get all groupchats on user
         */
    }
