package com.example.idatt2106_2022_05_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    // Recursive relationship
    @JoinColumn(name = "sub_category_id")
    private Category subCategory;

    @OneToOne(mappedBy = "subCategory", cascade = CascadeType.REMOVE)
    private Category category;

    //TODO create the other side of the one-to-one connection above
}
