package com.e_commerce.shambhu.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique verification token sent to the user's email.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    /**
     * User associated with this verification token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Token expiration timestamp.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Indicates whether the email has already been verified.
     */
    @Column(nullable = false)
    private Boolean verified = false;

    /**
     * Record creation timestamp.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last modification timestamp.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public EmailVerificationToken() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.verified == null) {
            this.verified = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}