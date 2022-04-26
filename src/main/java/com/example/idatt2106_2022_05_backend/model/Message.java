package com.example.idatt2106_2022_05_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="message_id", nullable=false)
    private long id;

    @Column(name = "content", nullable = false)
    private String content;

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @ManyToOne()
    @JoinColumn(name = "sender_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "groupId")
    private Group group;


}
