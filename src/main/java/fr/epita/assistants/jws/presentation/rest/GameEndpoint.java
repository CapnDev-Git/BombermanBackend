package fr.epita.assistants.jws.presentation.rest;

import fr.epita.assistants.jws.converter.Converter;
import fr.epita.assistants.jws.domain.service.GameService;
import fr.epita.assistants.jws.domain.service.PlayerService;
import fr.epita.assistants.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistants.jws.presentation.rest.request.JoinGameRequest;
import fr.epita.assistants.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistants.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GameListResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/games")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameEndpoint {
    @Inject
    GameService gameService;

    @Inject
    PlayerService playerService;

    @GET
    public List<GameListResponse> getGames() {
        return Converter.fromListGameEntityToListGameListResponse(gameService.getGames());
    }

    @POST
    @Transactional
    public GameDetailResponse createGame(@RequestBody CreateGameRequest request) throws IOException {
        if (request == null || request.name() == null)
            throw new BadRequestException("Bad request (request or name is null)");
        return Converter.fromGameEntityToGameDetailResponse(gameService.createGame(request.name()));
    }

    @GET
    @Path("/{gameId}")
    @Transactional
    public GameDetailResponse getGame(@PathParam("gameId") long id) {
        return Converter.fromGameEntityToGameDetailResponse(gameService.getGame(id));
    }

    @POST
    @Path("/{gameId}")
    @Transactional
    public GameDetailResponse joinGame(@PathParam("gameId") long id, @RequestBody JoinGameRequest request) {
        if (request == null || request.name() == null)
            throw new BadRequestException("The request or the player name is null");
        return Converter.fromGameEntityToGameDetailResponse(playerService.joinGame(id, request.name()));
    }

    @PATCH
    @Path("/{gameId}/start")
    @Transactional
    public GameDetailResponse startGame(@PathParam("gameId") long id){
        return Converter.fromGameEntityToGameDetailResponse(gameService.startGame(id));
    }

    @POST
    @Path("/{gameId}/players/{playerId}/move")
    @Transactional
    public GameDetailResponse placeBomb(@PathParam("gameId") long gameId, @PathParam("playerId") long playerId, @RequestBody MovePlayerRequest request) {
        if (request == null)
            throw new BadRequestException("The move request is null");
        return Converter.fromGameEntityToGameDetailResponse(playerService.movePlayer(gameId, playerId, request.posX(), request.posY()));
    }

    @POST
    @Path("/{gameId}/players/{playerId}/bomb")
    @Transactional
    public GameDetailResponse placeBomb(@PathParam("gameId") long gameId, @PathParam("playerId") long playerId, @RequestBody PutBombRequest request) {
        if (request == null)
            throw new BadRequestException("The bomb request is null");
        return Converter.fromGameEntityToGameDetailResponse(gameService.putBomb(gameId, playerId, request.posX(), request.posY()));
    }
}
