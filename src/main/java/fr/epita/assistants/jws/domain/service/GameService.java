package fr.epita.assistants.jws.domain.service;

import fr.epita.assistants.jws.converter.Converter;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.utils.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GameService {
    @Inject
    GameRepository gameRepository;

    @Inject
    PlayerRepository playerRepository;

    @ConfigProperty(name = "JWS_MAP_PATH")
    String mapPath;

    @ConfigProperty(name = "JWS_TICK_DURATION")
    long tickDuration;

    @ConfigProperty(name = "JWS_DELAY_BOMB")
    long delayBomb;

    List<Bomb> spawnedBombs = new ArrayList<>();
    List<Bomb> toRemoveBombs = new ArrayList<>();

    public List<GameEntity> getGames() {
        // Return the list of games (converted to entities)
        return Converter.toGameEntities(gameRepository.listAll());
    }

    public GameEntity getGame(long id) {
        // Find the game by id
        GameModel game = gameRepository.findById(id);

        // Check for no game found
        if (game == null)
            throw new NotFoundException("Cannot found game with this id");

        // Check all the currently spawned bombs
        boolean needsUpdate = false;
        Map map = new Map(game.getMap());
        for (Bomb bomb : spawnedBombs) {
            System.out.println("Time before explosion: " + (bomb.spawnTime() + (tickDuration * delayBomb) - System.currentTimeMillis()));

            // Check if the bomb has exploded
            if (System.currentTimeMillis() - bomb.spawnTime() >= (tickDuration * delayBomb)) {
                System.out.println("Bomb exploded!");

                // Explode the bomb & remove it from the list
                bomb.explode(game, map);
                toRemoveBombs.add(bomb);
                needsUpdate = true;
            }
        }

        // Remove the exploded bombs
        spawnedBombs.removeAll(toRemoveBombs);

        // Check if all the players are dead
        if (game.getPlayers().stream().noneMatch(PlayerModel::isAlive))
            game.setState(GameState.FINISHED);

        // Persist everything if needed
        if (needsUpdate) {
            // Update the players
            for (PlayerModel player : game.getPlayers())
                playerRepository.persist(player);

            // Update the map & persist the game
            game.setMap(map.toRLE());
            gameRepository.persist(game);
        }

        // Return the JSON response
        return Converter.toGameEntity(game);
    }

    public GameEntity createGame(String name) throws IOException {
        // Create & persist default player
        PlayerModel defaultPlayer = GameUtils.createPlayer(name, 0);

        // Create the default game & set the map
        GameModel game = GameUtils.createDefaultGame(defaultPlayer);
        game.setMap(Files.readAllLines(Paths.get(mapPath)));

        // Persist the default player and the game
        playerRepository.persist(defaultPlayer);
        gameRepository.persist(game);

        // Convert the game to an entity & return it
        return Converter.toGameEntity(game);
    }

    public GameEntity startGame(long id) {
        // Find the game by id
        GameModel game = GameUtils.findGameById(gameRepository, id);

        // Check for game already started
        if (game.getState() != GameState.STARTING)
            throw new NotFoundException("Game is already started!");

        // Change the state of the game depending on the number of players
        game.setState(game.getNbPlayers() == 1 ? GameState.FINISHED : GameState.RUNNING);

        // Persist the game
        gameRepository.persist(game);

        // Return the JSON response
        return Converter.toGameEntity(game);
    }

    public GameEntity putBomb(long gameId, long playerId, int posX, int posY) {
        // Find the game & player by id
        GameModel game = GameUtils.findGameById(gameRepository, gameId);
        PlayerModel player = GameUtils.findPlayerById(playerRepository, playerId);

        // Check for bomb allowance according to the tick duration & delay bomb
        if (player.getLastBomb() != null) {
            if (System.currentTimeMillis() - player.getLastBomb().getTime() < (tickDuration * delayBomb))
                throw new ClientErrorException("Player cannot place a bomb yet", 429);
        }

        // Check for game not running
        if (game.getState() != GameState.RUNNING)
            throw new BadRequestException("Game is not running");

        // Check for player already dead
        if (!player.isAlive())
            throw new BadRequestException("Player is already dead");

        // Check if the given coords are valid (i.e. the ones of the player)
        if (player.getPosX() != posX || player.getPosY() != posY)
            throw new BadRequestException("Invalid bomb placement");

        // Set the bomb in the map (change the block type & map in the game)
        Map map = new Map(game.getMap());
        map.set(posX, posY, BlockType.BOMB);
        game.setMap(map.toRLE());

        // Set the last bomb time of the player
        long currentTime = System.currentTimeMillis();
        player.setLastBomb(new java.sql.Timestamp(currentTime));
        spawnedBombs.add(new Bomb(posX, posY, currentTime));

        // Persist the player & game
        playerRepository.persist(player);
        gameRepository.persist(game);

        // Return the JSON response
        return Converter.toGameEntity(game);
    }
}