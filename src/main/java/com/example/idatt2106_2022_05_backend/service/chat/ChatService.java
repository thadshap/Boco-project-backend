package com.example.idatt2106_2022_05_backend.service.chat;

import com.example.idatt2106_2022_05_backend.dto.chat.ListGroupDto;
import com.example.idatt2106_2022_05_backend.dto.chat.MessageDto;
import com.example.idatt2106_2022_05_backend.dto.chat.PrivateGroupDto;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    Response getAllMessagesByGroupId(long groupId);

    // MessageDto saveMessage(MessageDto message, long groupId);

    Response getChat(long id);

    Response createTwoUserGroup(PrivateGroupDto privateGroupDto);

    Response getGroupChatsBasedOnUserId(long id);

    Response removeUserFromGroupById(long groupId, long userId);

    Response addUserToGroupById(long groupId, long userId);

    void broadcast(MessageDto message);

    Response createGroupFromUserIds(ListGroupDto listGroupDto);

    Response changeGroupNameFromGroupId(long groupId, String newName);

    MessageDto sendMessage(Long groupId, MessageDto content);

    Response addUserToGroupByEmail(long groupId, String email);

    // Response getGroupChatsBasedOnUserId(long id);

}
