package com.canaiguess.api.repository;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.id NOT IN (SELECT ig.image.id FROM ImageGame ig WHERE ig.game.user = :user)")
    List<Image> findUnplayedImagesByUser(@Param("user") User user);

    Optional<Image> findByFilename(String filename);

}
