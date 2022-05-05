package com.example.idatt2106_2022_05_backend.dto.chat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data transfer object to create a group from list of emails")
public class EmailListGroupDto {
    private String groupName;
    private Set<String> emails;
}
