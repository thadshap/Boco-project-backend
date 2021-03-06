package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Group;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findById(long id);
}
