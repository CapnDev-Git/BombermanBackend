package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.utils.GameState;

public record GameListResponse(long id, int players, GameState state) {
}
