package com.canaiguess.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ImageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Image image;

    @ManyToOne(optional = false)
    private User user;

    @Column
    private String title;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    @Column
    boolean resolved = false;

    @ManyToOne
    private User reviewer;

}
