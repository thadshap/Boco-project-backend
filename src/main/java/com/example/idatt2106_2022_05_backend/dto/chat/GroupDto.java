package com.example.idatt2106_2022_05_backend.dto.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data transfer object of group, used for creating groups and returning available groups to user")
public class GroupDto {
    @ApiModelProperty(notes = "id of group")
    private long groupId;

    @ApiModelProperty(notes = "name of group")
    private String groupName;
}
