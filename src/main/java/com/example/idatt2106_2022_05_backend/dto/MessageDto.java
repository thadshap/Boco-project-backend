package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private long fromUserId;
    private long groupId;
    private String content;
    private long toUserId;
}
