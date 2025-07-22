package com.canaiguess.api.model;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // shall NOT be exposed anywhere

    @Column(unique = true, nullable = false)
    private String publicId; // can be exposed to frontend

    private int batchCount;

    private int batchSize;

    private int difficulty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int currentBatch;

    @Transient
    public boolean isFinished() {
        return currentBatch > batchCount;
    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageGame> imageGames;

    @Column
    private Integer score; // needed for last 10 user games

    @CreationTimestamp
    private LocalDateTime createdAt; // needed for last 10 user games

    @Column(nullable = false)
    private Integer totalGuesses = 0; // derived field

    @Column(nullable = false)
    private Integer correctGuesses = 0; // derived field

    @Transient
    public double getAccuracy() {
        return totalGuesses > 0 ? ((double) correctGuesses / totalGuesses) * 100.0 : 0.0;
    }

    @PrePersist
    public void ensurePublicId() {
        if (this.publicId == null) {
            this.publicId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        }
    }

}
