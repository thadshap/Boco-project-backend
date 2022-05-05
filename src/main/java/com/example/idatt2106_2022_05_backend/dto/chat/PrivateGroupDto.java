package com.example.idatt2106_2022_05_backend.dto.chat;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data transfer object to create a group from two users")
public class PrivateGroupDto {
    private String groupName;
    private long userOneId;
    private long userTwoId;
}
