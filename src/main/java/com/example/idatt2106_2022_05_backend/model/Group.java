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
@Table(name="groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="group_id", nullable=false)
    private Long groupId;

    @Column(name = "name")
    private String name;

    @ManyToMany()
    @JoinTable(
            name="user_group",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    private Set<User> users;
}
