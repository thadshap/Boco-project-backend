package com.example.idatt2106_2022_05_backend.dto.chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailListGroupDto {
    private String groupName;
    private Set<String> emails;
}
