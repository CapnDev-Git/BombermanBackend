package fr.epita.assistants.jws.converter;

import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.domain.entity.PlayerEntity;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GameListResponse;
import fr.epita.assistants.jws.presentation.rest.response.PlayerResponse;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    private static List<PlayerEntity> toPlayerEntities(List<PlayerModel> players) {
        return players.stream().map(Converter::toPlayerEntity).toList();
    }

    public static List<GameEntity> toGameEntities(List<GameModel> gameModels) {
        return gameModels.stream().map(Converter::toGameEntity).toList();
    }

    public static GameEntity toGameEntity(GameModel game) {
        GameEntity gameEntity = new GameEntity();
        gameEntity.setId(game.getId());
        gameEntity.setStartTime(game.getStartTime());
        gameEntity.setState(game.getState());
        gameEntity.setMap(game.getMap());
        gameEntity.setPlayers(Converter.toPlayerEntities(game.getPlayers()));
        return gameEntity;
    }

    public static PlayerEntity toPlayerEntity(PlayerModel player) {
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setId(player.getId());
        playerEntity.setLastBomb(player.getLastBomb());
        playerEntity.setLastMovement(player.getLastMovement());
        playerEntity.setLives(player.getLives());
        playerEntity.setName(player.getName());
        playerEntity.setPosX(player.getPosX());
        playerEntity.setPosY(player.getPosY());
        return playerEntity;
    }

    public static List<PlayerResponse> fromListPlayerEntityToListPlayerResponse(List<PlayerEntity> players) {
        return new ArrayList<>(players
                .stream()
                .map(player -> new PlayerResponse(
                        player.getId(),
                        player.getName(),
                        player.getLives(),
                        player.getPosX(),
                        player.getPosY())
                )
                .toList()
        );
    }

    public static GameDetailResponse fromGameEntityToGameDetailResponse(GameEntity gameEntity) {
        // Make sure the map is initialized
        Hibernate.initialize(gameEntity.getMap());

        // Convert the list of players to a list of player responses
        List<PlayerResponse> playerResponse = fromListPlayerEntityToListPlayerResponse(gameEntity.getPlayers());
        return new GameDetailResponse(gameEntity.getStartTime(), gameEntity.getState(), playerResponse, gameEntity.getMap(), gameEntity.getId());
    }

    public static List<GameListResponse> fromListGameEntityToListGameListResponse(List<GameEntity> games) {
        return new ArrayList<>(games
                .stream()
                .map(game -> new GameListResponse(game.getId(), game.getPlayers().size(), game.getState()))
                .toList()
        );
    }
}
