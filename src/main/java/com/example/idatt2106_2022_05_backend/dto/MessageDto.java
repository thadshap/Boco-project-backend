package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String content;
    private String timeStamp;
    //User sending
    private long userId;
    private String firstName;
    private String lastName;
}
