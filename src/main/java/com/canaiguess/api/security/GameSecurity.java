package com.canaiguess.api.security;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("gameSecurity")
public class GameSecurity {

    private final GameRepository gameRepository;

    public GameSecurity(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public boolean isOwner(String gameId, Authentication authentication) {
        Game game = gameRepository.findByPublicId(gameId).orElse(null);
        if (game == null) return false;

        if (game.getUser() == null) {
            // requests with no authentication can access anonymous games,
            // although it is not checked weather it is the same anonymous user
            // but as the public id is unguessable, attacks shouldn't occur
            return authentication == null;
        }

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return false;
        }

        return game.getUser().getId().equals(user.getId());
    }

}

