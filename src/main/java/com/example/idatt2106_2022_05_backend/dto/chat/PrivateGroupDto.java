package com.example.idatt2106_2022_05_backend.dto.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data transfer object to create a group from two users")
public class PrivateGroupDto {
    @ApiModelProperty(notes = "name of new group")
    private String groupName;

    @ApiModelProperty(notes = "id of user in new group")
    private long userOneId;

    @ApiModelProperty(notes = "id of user in new group")
    private long userTwoId;
}
