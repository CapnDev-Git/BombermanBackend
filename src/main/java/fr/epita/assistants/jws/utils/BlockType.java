package fr.epita.assistants.jws.utils;


import lombok.Getter;

@Getter
public enum BlockType {
    WOOD('W'),
    METAL('M'),
    BOMB('B'),
    GROUND('G');

    private final char type;

    BlockType(char type) {
        this.type = type;
    }

    public static BlockType from(char type) {
        for (BlockType blockType : BlockType.values()) {
            if (blockType.getType() == type) {
                return blockType;
            }
        }
        return null;
    }

    public static boolean isDestructible(BlockType blockType) {
        return blockType == BlockType.WOOD;
    }

    public static boolean isWall(BlockType blockType) {
        return blockType != BlockType.GROUND;
    }
}
