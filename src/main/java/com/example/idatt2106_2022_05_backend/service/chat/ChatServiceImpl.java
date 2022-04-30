package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.OutputMessage;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.GroupRepository;
import com.example.idatt2106_2022_05_backend.repository.MessageRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
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

    private Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

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
    public void broadcast(Message message){
        OutputMessage outputMessage = new OutputMessage();

        String text = new String((byte[])message.getPayload(), StandardCharsets.UTF_8);
        outputMessage.setText(text);

        //TODO: Add user check and set in outputmessage
        //outputMessage.setFrom();
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        outputMessage.setTime(time);
        messageRepository.save(outputMessage);
        //TODO: send to correct recipient
        this.simpMessagingTemplate.convertAndSend("/topic/messages", outputMessage);

    }

    @Override
    public Response getChat(long id){
        Group group = getGroup(id);
        List<OutputMessage> messageDtos = messageRepository.findAllByGroup(group).stream().collect(Collectors.toList());
        messageDtos.sort(Comparator.comparing(OutputMessage::getTime));
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
