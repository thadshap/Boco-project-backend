package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.Message;
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
import java.util.Set;
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
    private SimpMessagingTemplate simpMessagingTemplate;

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
    public Response saveMessage(MessageDto message) {
        Message message1 = new Message();

        if(message.getContent().length()>280){
            return new Response("Innholdet i meldingen er for langt", HttpStatus.BAD_REQUEST);
        }
        message1.setContent(message.getContent());
        message1.setUser(getUser(message.getToUserId()));
        message1.setGroup(getGroup(message.getGroupId()));

        //setting timestamp
        Timestamp current = Timestamp.from(Instant.now());
        message1.setTimestamp(current);

        messageRepository.save(message1);

        simpMessagingTemplate.convertAndSend("/topic/group/" + message.getGroupId(), message);

        return new Response("Meldingen ble lagret", HttpStatus.OK);
    }

    @Override
    public Response getChat(long id){
        Group group = getGroup(id);
        List<Message> messageDtos = messageRepository.findAllByGroup(group).stream().collect(Collectors.toList());
        messageDtos.sort(Comparator.comparing(Message::getTimestamp));
        return new Response(messageDtos.stream()
                .map(message -> modelMapper.map(message, MessageDto.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

        /**
         * 1. Metode til å sende melding
         * 3. Metode til å hente en chat
         * TODO: paginate og sorter chat
         * 4. Get all groupchats on user
         */
    }
