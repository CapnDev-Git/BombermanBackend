package fr.epita.assistants.jws.domain.entity;

import fr.epita.assistants.jws.utils.GameState;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEntity {
    private long id;
    private GameState state;
    private java.util.Date startTime;
    private List<String> map = new ArrayList<>();
    private List<PlayerEntity> players = new ArrayList<>();
}