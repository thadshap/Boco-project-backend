package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
