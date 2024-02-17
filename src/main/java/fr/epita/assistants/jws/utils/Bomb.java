package fr.epita.assistants.jws.utils;

import fr.epita.assistants.jws.data.model.GameModel;
import groovy.lang.Tuple2;

public record Bomb(int x, int y, long spawnTime) {
    public void explode(GameModel game, Map map) { // map is converted (not RLE, so good)
        // Explode, destroy & damage in all directions
        explodeDirection(game, map, Direction.UP, x, y); // UP
        explodeDirection(game, map, Direction.DOWN, x, y); // DOWN
        explodeDirection(game, map, Direction.LEFT, x, y); // LEFT
        explodeDirection(game, map, Direction.RIGHT, x, y); // RIGHT

        // Explode the bomb itself (destroy it)
        map.set(x, y, BlockType.GROUND);
    }

    private void explodeDirection(GameModel game, Map map, Direction direction, int x, int y) {
        // Update the coordinates depending on the direction
        Tuple2<Integer, Integer> coords = GameUtils.getCoordsFromDirection(direction, x, y);
        int newX = coords.getV1();
        int newY = coords.getV2();

        // Get the block type & explode if destructible
        BlockType block = map.get(newX, newY);
        if (BlockType.isDestructible(block))
            map.set(newX, newY, BlockType.GROUND);

        // Damage all players at the damage coordinates (given by the direction)
        game.getPlayers().stream()
                .filter(player -> player.getPosX() == newX && player.getPosY() == newY)
                .forEach(player -> player.setLives(player.getLives() - 1));
    }
}
