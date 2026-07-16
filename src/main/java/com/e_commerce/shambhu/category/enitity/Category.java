package com.e_commerce.shambhu.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_slug",
                        columnNames = "slug"
                )
        }
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 180)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Self-referencing relationship.
     * Null indicates a root category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    /**
     * Child categories.
     */
    @OrderBy("displayOrder ASC, name ASC")
    @OneToMany(
            mappedBy = "parentCategory",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    private List<Category> childCategories = new ArrayList<>();

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public Category() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.active == null) {
            this.active = true;
        }

        if (this.deleted == null) {
            this.deleted = false;
        }

        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // -----------------------------
    // Helper Methods
    // -----------------------------

    public void addChild(Category child) {
        child.setParentCategory(this);
        this.childCategories.add(child);
    }

    public void setParentCategory(com.e_commerce.shambhu.category.entity.Category category) {
    }

    public void removeChild(Category child) {
        child.setParentCategory(null);
        this.childCategories.remove(child);
    }
}