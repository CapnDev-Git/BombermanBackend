package fr.epita.assistants.jws.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "player")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "lastbomb")
    public Timestamp lastBomb;

    @Column(name = "lastmovement")
    public Timestamp lastMovement;

    public int lives;
    public String name;

    @Column(name = "posx")
    public int posX;

    @Column(name = "posy")
    public int posY;

    @ManyToOne
    @JoinColumn(name = "game_id")
    public GameModel game;

    @ManyToOne
    @JoinTable(
            name = "game_player",
            joinColumns = @JoinColumn(name = "players_id"),
            inverseJoinColumns = @JoinColumn(name = "gamemodel_id")
    )
    public GameModel game_player;

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}