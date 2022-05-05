package com.example.idatt2106_2022_05_backend.dto.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "Data transfer object for message, used for sending messages with websocket")
public class MessageDto {
    @ApiModelProperty(notes = "text content of message")
    private String content;

    @ApiModelProperty(notes = "time stamp of message when sent")
    private String timeStamp;

    @ApiModelProperty(notes = "id of message sender")
    private long userId;

    @ApiModelProperty(notes = "first name of sender")
    private String firstName;

    @ApiModelProperty(notes = "last name of sender")
    private String lastName;

    @ApiModelProperty(notes = "image type of sender picture")
    private String type;

    @ApiModelProperty(notes = "base64 of sender picture")
    private String base64;
}
