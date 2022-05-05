package com.example.idatt2106_2022_05_backend.model;

import lombok.*;


import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="groupss")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id", nullable=false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany()
    @JoinTable(
            name="user_group",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @ToString.Exclude
    private Set<User> users;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private Set<Message> messages;

}
