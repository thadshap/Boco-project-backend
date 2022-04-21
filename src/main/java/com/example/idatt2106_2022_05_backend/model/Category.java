package com.example.idatt2106_2022_05_backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Recursive one-to-many relationship // todo some suspicions regarding the tags --> should they be the opposite?
     */
    @OneToMany(mappedBy = "mainCategory")
    private Set<Category> subCategories;

    @ManyToOne
    private Category mainCategory;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Set<Ad> ads;
}
