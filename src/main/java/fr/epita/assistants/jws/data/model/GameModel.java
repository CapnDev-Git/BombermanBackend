package fr.epita.assistants.jws.data.model;

import fr.epita.assistants.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "game")
@NoArgsConstructor
@AllArgsConstructor
public class GameModel {
    public @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

    public GameState state;

    @Column(name = "starttime")
    public Timestamp startTime;

    @ElementCollection
    @CollectionTable(name = "game_map",
            joinColumns = @JoinColumn(name = "gamemodel_id"))
    public List<String> map = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    public List<PlayerModel> players = new ArrayList<>();

    public void addPlayer(PlayerModel player) {
        players.add(player);
        player.setGame(this);
    }

    public int getNbPlayers() {
        return players.size();
    }
}