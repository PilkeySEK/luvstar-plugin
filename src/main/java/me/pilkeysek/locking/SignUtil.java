package me.pilkeysek.locking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.block.BlockFace;

import me.pilkeysek.Util;

public class SignUtil {
    public static byte blockFaceToSignRotationData(BlockFace blockFace) {
        switch (blockFace) {
            case NORTH:
                return 4;
            case SOUTH:
                return 5;
            case EAST:
                return 2;
            case WEST:
                return 3;
            default:
                return 0;
        }
    }

    public static BlockFace standingSignRotationDataToBlockFace(byte rotationData) {
        /*
        switch(rotationData) {
            case 0x0: return BlockFace.SOUTH;
            case 0x4: return BlockFace.WEST;
            case 0x8: return BlockFace.NORTH;
            case 0xc: return BlockFace.EAST;
            default: return BlockFace.SELF;
        }
        */
        switch(rotationData) {
            case 0x0: return BlockFace.WEST;
            case 0x4: return BlockFace.NORTH;
            case 0x8: return BlockFace.EAST;
            case 0xc: return BlockFace.SOUTH;
            default: return BlockFace.SELF;
        }
    }

    public static BlockFace invertBlockFace(BlockFace face) {
        switch (face) {
            case NORTH: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.NORTH;
            case EAST: return BlockFace.WEST;
            case WEST: return BlockFace.EAST;
            default: return BlockFace.SELF;
        }
    }

    public static byte standingToWallSignData(byte standingSignData) {
        switch(standingSignData) {
            case 0x8: return 0x2;
            case 0x4: return 0x4;
            case 0x0: return 0x3;
            case 0xC: return 0x5;
            default: return Byte.MAX_VALUE;
        }
    }

    public static boolean isLockSign(List<String> lines) {
        String firstLine = lines.get(0);
        return firstLine.equalsIgnoreCase("[private]") || firstLine.equalsIgnoreCase("[more users]") || firstLine.equalsIgnoreCase("[public]");
    }
    public static boolean isLockSign(String[] lines) {
        String firstLine = lines[0];
        return firstLine.equalsIgnoreCase("[private]") || firstLine.equalsIgnoreCase("[more users]") || firstLine.equalsIgnoreCase("[public]");
    }

    public static ArrayList<String> getSignPlayerList(String[] lines) {
        ArrayList<String> arrLines = new ArrayList<>();

        for(int i = isLockSign(lines) ? 1 : 0 /* If sign has something such as [private] as the first line, skip it */; i < lines.length; i++) {
            String line = lines[i].trim();
            if(line.isEmpty()) continue;
            arrLines.add(line);
        }

        Collections.reverse(arrLines);
        return arrLines;
    }
    public static ArrayList<String> getSignPlayerList(List<String> lines) {
        return getSignPlayerList(Util.listToStringArray(lines));
    }
}
