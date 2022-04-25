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
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "classname")
    private String classname;

    /**
     * Recursive one-to-many relationship
     */
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "mainCategory")
    private Set<Category> subCategories;

    @ManyToOne
    private Category mainCategory;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Set<Ad> ads;
}
