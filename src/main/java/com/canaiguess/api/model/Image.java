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

    private boolean fake; // true = ai, false = real

    private int total; // total guesses

    private int correct; // correct guesses

}
