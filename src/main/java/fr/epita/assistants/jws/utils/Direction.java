package fr.epita.assistants.jws.utils;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    static Direction getDirection(int dx, int dy) {
        // Check for the direction of the move
        if (dy == 1) return Direction.DOWN;
        if (dy == -1) return Direction.UP;
        if (dx == 1) return Direction.RIGHT;
        if (dx == -1) return Direction.LEFT;

        // Return null if no direction found, should never happen
        return null;
    }
}
