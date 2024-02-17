package fr.epita.assistants.jws.utils;

import fr.epita.assistants.jws.data.model.GameModel;

import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import groovy.lang.Tuple2;

import javax.ws.rs.NotFoundException;
import java.util.List;

public class GameUtils {
    public static PlayerModel createPlayer(String name, int nbConnectedPlayers) {
        // Create the instance & set the name & lives
        PlayerModel p = new PlayerModel();
        p.setName(name);
        p.setLives(3);

        // Set the position of the player
        switch (nbConnectedPlayers) {
            case 0 -> p.setPosition(1, 1);
            case 1 -> p.setPosition(15, 1);
            case 2 -> p.setPosition(15, 13);
            case 3 -> p.setPosition(1, 13);
            default -> throw new IllegalArgumentException("Should never happen!");
        }

        // Return the player
        return p;
    }

    public static GameModel createDefaultGame(PlayerModel defaultPlayer) {
        // Instantiate a new game
        GameModel game = new GameModel();
        game.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
        game.setState(GameState.STARTING);
        game.addPlayer(defaultPlayer);

        // Return the game
        return game;
    }

    public static GameModel findGameById(GameRepository gameRepository, long gameId) {
        // Find the game by id
        GameModel game = gameRepository.findById(gameId);

        // Check for no game found
        if (game == null)
            throw new NotFoundException("Game with this ID does not exist");

        // Return the game if found
        return game;
    }

    public static PlayerModel findPlayerById(PlayerRepository playerRepository, long playerId) {
        // Find the player by id
        PlayerModel player = playerRepository.findById(playerId);

        // Check for no player found
        if (player == null)
            throw new NotFoundException("Player with this ID does not exist");

        // Return the player if found
        return player;
    }

    public static boolean isMoveValid(PlayerModel player, List<String> mapRLE, int targetX, int targetY) {
        // Get the player's position
        int playerPosX = player.getPosX();
        int playerPosY = player.getPosY();

        // Detect the direction of the move
        int dx = targetX - playerPosX;
        int dy = targetY - playerPosY;

        // Check for the move being valid (not diagonal, not null)
        if ((dx == 0 && dy == 0) || (dx != 0 && dy != 0))
            return false;

        // Get the direction of the move
        Direction direction = Direction.getDirection(dx, dy);
        System.out.println("Direction: " + direction);
        assert direction != null;

        // Get the block type from the map
        Map map = new Map(mapRLE);
        BlockType blockType = map.get(targetX, targetY);
        System.out.println("Block type: " + blockType);
        return !BlockType.isWall(blockType);
    }

    public static Tuple2<Integer, Integer> getCoordsFromDirection(Direction direction, int x, int y) {
        // Get the coordinates from the direction
        switch (direction) {
            case UP -> y--;
            case DOWN -> y++;
            case LEFT -> x--;
            case RIGHT -> x++;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }

        // Return the coordinates
        return new Tuple2<>(x, y);
    }
}
