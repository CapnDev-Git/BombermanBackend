package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.utils.GameState;

import java.util.List;

public record GameDetailResponse(java.util.Date startTime, GameState state, List<PlayerResponse> players, List<String> map, long id) {
}