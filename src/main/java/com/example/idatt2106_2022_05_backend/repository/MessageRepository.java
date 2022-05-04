package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Set<Message> findAllByGroup(Group group);
}
