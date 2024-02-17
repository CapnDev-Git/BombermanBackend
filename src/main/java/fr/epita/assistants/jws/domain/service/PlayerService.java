package fr.epita.assistants.jws.domain.service;

import fr.epita.assistants.jws.converter.Converter;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.utils.GameState;
import fr.epita.assistants.jws.utils.GameUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;

@ApplicationScoped
public class PlayerService {
    @Inject
    GameRepository gameRepository;

    @Inject
    PlayerRepository playerRepository;

    @ConfigProperty(name = "JWS_TICK_DURATION")
    long tickDuration;

    @ConfigProperty(name = "JWS_DELAY_MOVEMENT")
    long delayMovement;

    public GameEntity joinGame(long id, String name) {
        // Find the game by id
        GameModel game = GameUtils.findGameById(gameRepository, id);

        // Check for the game being already started OR full
        int nbPlayers = game.getNbPlayers();
        if (game.getState() != GameState.STARTING || nbPlayers == 4)
            throw new BadRequestException("Game is already started");

        // Create new player, persist it and add it to the game
        PlayerModel newPlayer = GameUtils.createPlayer(name, nbPlayers);
        game.addPlayer(newPlayer);

        // Persist the new player and the game
        playerRepository.persist(newPlayer);
        gameRepository.persist(game);

        // Return the JSON response
        return Converter.toGameEntity(game);
    }

    public GameEntity movePlayer(long gameId, long playerId, int destX, int destY) {
        // Find the game & player by id
        GameModel game = GameUtils.findGameById(gameRepository, gameId);
        PlayerModel player = GameUtils.findPlayerById(playerRepository, playerId);

        // Check for move allowance according to the tick duration & delay movement
        if (player.getLastMovement() != null) {
            if (System.currentTimeMillis() - player.getLastMovement().getTime() < (tickDuration * delayMovement))
                throw new ClientErrorException("Player cannot move yet", 429);
        }

        // Check for game not running
        if (game.getState() != GameState.RUNNING)
            throw new BadRequestException("Game is not running");

        // Check for player already dead
        if (!player.isAlive())
            throw new BadRequestException("Player is already dead");

        // Check for invalid move
        if (!GameUtils.isMoveValid(player, game.getMap(), destX, destY))
            throw new BadRequestException("Invalid move for player");

        // Set the new position of the player
        player.setPosition(destX, destY);
        player.setLastMovement(new java.sql.Timestamp(System.currentTimeMillis()));

        // Persist the player & game
        playerRepository.persist(player);
        gameRepository.persist(game);

        // Return the JSON response
        return Converter.toGameEntity(game);
    }
}