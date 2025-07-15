package com.canaiguess.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    private String filename;
    private boolean image_type; // true = ai, false = real
    private int total_guesses;
    private int correct_guesses;

}
