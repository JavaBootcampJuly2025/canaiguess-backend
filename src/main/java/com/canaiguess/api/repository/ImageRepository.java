package com.canaiguess.api.repository;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // find images played by no one
    @Query("""
        SELECT i FROM Image i
        WHERE i.total = 0
    """)
    List<Image> findFreshImages(Pageable pageable);

    // find images played by someone but not the user
    @Query("""
        SELECT i FROM Image i
        WHERE i.total > 0
        AND i.id NOT IN (
             SELECT ig.image.id
             FROM ImageGame ig
             WHERE ig.game.user = :user
       )
        ORDER BY ABS((1.0 - (i.correct * 1.0 / i.total)) - :targetDifficulty)
    """)
    List<Image> findPlayedByOthersSortedByDifficulty(
            @Param("user") User user,
            @Param("targetDifficulty") double targetDifficulty,
            Pageable pageable
    );

    // find images played by the user
    @Query("""
        SELECT i FROM Image i
        WHERE i.total > 0
        AND i.id IN (
             SELECT ig.image.id
             FROM ImageGame ig
             WHERE ig.game.user = :user
       )
        ORDER BY ABS((1.0 - (i.correct * 1.0 / i.total)) - :targetDifficulty)
    """)
    List<Image> findPlayedByUserSortedByDifficulty(
            @Param("user") User user,
            @Param("targetDifficulty") double targetDifficulty,
            Pageable pageable
    );

    // fresh real or fake
    @Query("SELECT i FROM Image i WHERE i.total = 0 AND i.fake = :isFake")
    List<Image> findFreshByFakeness(@Param("isFake") boolean isFake, Pageable pageable);

    // played by others, sorted by difficulty
    @Query("""
        SELECT i FROM Image i
        WHERE i.total > 0 AND i.fake = :isFake
        AND NOT EXISTS (
            SELECT 1 FROM ImageGame ig
            JOIN ig.game g
            WHERE ig.image = i AND g.user = :user
        )
        ORDER BY ABS((1.0 - (i.correct * 1.0 / i.total)) - :targetDifficulty)
    """)
    List<Image> findPlayedByOthersByFakeness(
            @Param("user") User user,
            @Param("targetDifficulty") double targetDifficulty,
            @Param("isFake") boolean isFake,
            Pageable pageable
    );

    // played by user
    @Query("""
        SELECT i FROM Image i
        WHERE i.total > 0 AND i.fake = :isFake
        AND EXISTS (
            SELECT 1 FROM ImageGame ig
            JOIN ig.game g
            WHERE ig.image = i AND g.user = :user
        )
        ORDER BY ABS((1.0 - (i.correct * 1.0 / i.total)) - :targetDifficulty)
    """)
    List<Image> findPlayedByUserByFakeness(
            @Param("user") User user,
            @Param("targetDifficulty") double targetDifficulty,
            @Param("isFake") boolean isFake,
            Pageable pageable
    );


    Optional<Image> findByPublicId(String publicId);
}
