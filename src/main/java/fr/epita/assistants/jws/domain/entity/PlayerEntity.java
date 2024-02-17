package fr.epita.assistants.jws.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    private long id;
    private Timestamp lastBomb;
    private Timestamp lastMovement;
    private int lives;
    private String name;
    private int posX;
    private int posY;
    private GameEntity game;
    private GameEntity game_player;
}
