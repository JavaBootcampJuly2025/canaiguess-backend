package com.canaiguess.api.repository;

import com.canaiguess.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findAll(); // for accuracy leaderboard

    // Use JOIN FETCH to eagerly load games to avoid LazyInitializationException
    // when mapping users to UserDTO in LeaderboardService.
    @Query("""
  SELECT DISTINCT u
  FROM User u
  LEFT JOIN FETCH u.games
  ORDER BY u.score DESC
""")
    List<User> findTop10ByOrderByScoreDesc();
}
