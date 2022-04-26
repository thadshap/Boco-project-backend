package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT a FROM Message a WHERE a.group.id= :id")
    Set<Message> getMessagesByGroupId(@Param("id") long id);
}
