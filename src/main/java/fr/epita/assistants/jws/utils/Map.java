package fr.epita.assistants.jws.utils;

import groovy.lang.Tuple2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Map {
    private List<String> map;
    private int width;
    private int height;

    public Map(List<String> linesRLE) {
        this.map = new ArrayList<>();

        // Convert the RLE to the map
        for (String line : linesRLE) {
            StringBuilder newLine = new StringBuilder();
            for (int j = 0; j < line.length(); j += 2) {
                int count = line.charAt(j) - '0';
                char c = line.charAt(j + 1);
                newLine.append(String.valueOf(c).repeat(Math.max(0, count)));
            }
            map.add(newLine.toString());
        }

        // Set the width & height
        this.width = map.get(0).length();
        this.height = map.size();
    }

    public BlockType get(int x, int y) {
        return BlockType.from(map.get(y).charAt(x));
    }

    public void set(int posX, int posY, BlockType blockType) {
        // Set the block type
        StringBuilder line = new StringBuilder(map.get(posY));
        line.setCharAt(posX, blockType.getType());
        map.set(posY, line.toString());
    }

    public List<String> toRLE() {
        // Convert the map to RLE format
        List<String> linesRLE = new ArrayList<>();
        for (String line : map) {
            StringBuilder newLine = new StringBuilder();
            char c = line.charAt(0);
            int count = 1;
            for (int j = 1; j < line.length(); j++) {
                if (line.charAt(j) == c) {
                    if (count == 9) {
                        newLine.append(count).append(c);
                        count = 1;
                    } else {
                        count++;
                    }
                } else {
                    newLine.append(count).append(c);
                    c = line.charAt(j);
                    count = 1;
                }
            }
            newLine.append(count).append(c);
            linesRLE.add(newLine.toString());
        }



        // Return the RLE
        return linesRLE;
    }
}
