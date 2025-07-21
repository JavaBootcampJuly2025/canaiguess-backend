package com.canaiguess.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

@Entity
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // shall NOT be exposed anywhere

    @Column(unique = true, nullable = false)
    private String publicId; // can be exposed to frontend

    private String url;

    private boolean fake; // true = ai, false = real

    private int total; // total guesses

    private int correct; // correct guesses

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageGame> imageGames;

    @PrePersist
    public void ensurePublicId() {
        if (this.publicId == null) {
            this.publicId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        }
    }
}
