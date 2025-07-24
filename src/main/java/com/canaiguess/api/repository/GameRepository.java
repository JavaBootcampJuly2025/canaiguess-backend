package com.canaiguess.api.repository;

import com.canaiguess.api.model.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByUserIdOrderByCreatedAtDesc(long userId, Pageable pageable);

    Optional<Game> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);

    // Count games directly in the DB to avoid LazyInitializationException
    // from user.getGames().size()
    @Query("""
      SELECT COUNT(g)
      FROM Game g
      WHERE g.user.username = :username
    """)

    int countGamesByUsername(String username);
}
