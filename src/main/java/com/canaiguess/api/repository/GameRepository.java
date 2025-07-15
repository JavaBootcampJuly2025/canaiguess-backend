package com.canaiguess.api.repository;

import com.canaiguess.api.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {

}
