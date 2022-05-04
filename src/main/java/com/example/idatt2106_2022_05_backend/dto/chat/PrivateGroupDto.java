package com.example.idatt2106_2022_05_backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateGroupDto {
    private String groupName;
    private long userOneId;
    private long userTwoId;
}
