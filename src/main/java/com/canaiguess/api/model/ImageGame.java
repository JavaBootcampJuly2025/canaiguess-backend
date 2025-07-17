package com.canaiguess.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageGame {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Image image;

    private int batchNumber;

    private boolean userGuessedCorrectly; // to track user specific results (for drill down)

}
