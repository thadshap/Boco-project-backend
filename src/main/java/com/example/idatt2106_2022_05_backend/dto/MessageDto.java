package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private long user_id;
    //private long groupId;  //Maybe we wont need this
    private String content;
    private String timestamp;
}
