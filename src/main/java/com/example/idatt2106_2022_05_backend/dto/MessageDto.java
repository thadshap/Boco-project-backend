package com.example.idatt2106_2022_05_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.Times;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private long fromUserId;
    private long groupId;  //Maybe we will
    private String content;
    private long toUserId;
    private Timestamp timestamp;
}
