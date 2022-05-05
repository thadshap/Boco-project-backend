package com.example.idatt2106_2022_05_backend.dto.chat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Data transfer object to return after group created")
public class EmailListGroupReturnDto {

    @ApiModelProperty(notes = "true if group created, false if not")
    private boolean succeeded;

    @ApiModelProperty(notes = "name of the created group")
    private String groupName;

    @ApiModelProperty(notes = "id of the created group")
    private Long groupId;

    @ApiModelProperty(notes = "emails of users not added in group")
    private Set<String> failedEmails;
}