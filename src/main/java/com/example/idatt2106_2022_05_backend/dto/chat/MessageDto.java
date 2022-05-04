package com.example.idatt2106_2022_05_backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageDto {

    private String content;
    private String timeStamp;
    // User sending
    private long userId;
    private String firstName;
    private String lastName;
}
