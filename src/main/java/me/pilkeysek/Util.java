package me.pilkeysek;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import me.pilkeysek.data.ChestLockData;
import me.pilkeysek.data.ChestLockUpdateResult;

public class Util {
    public static String locToIntString(Location loc) {
        return Integer.toString((int) loc.getX()) + " " + Integer.toString((int) loc.getY()) + " "
                + Integer.toString((int) loc.getZ());
    }

    public static ChestLockUpdateResult updateChestLock(Block chest, String owner, boolean locked,
            boolean ignoreDifferentOwner) {
        ChestLockUpdateResult result = new ChestLockUpdateResult(chest.getLocation());
        ChestLockData existingData = LuvstarPlugin.instance.db.getChestLockData(chest.getLocation());
        if (!ignoreDifferentOwner && existingData != null && !existingData.owner.equalsIgnoreCase(owner)) {
            result.successfullyUpdated = false;
            result.playerIsNotOwner = true;
            result.owner = existingData.owner;
            return result;
        }
        if (existingData != null && existingData.owner.equals(owner) && existingData.locked == locked) {
            result.successfullyUpdated = false;
            result.isAlreadyInThisState = true;
            return result;
        }
        int dbRes = LuvstarPlugin.instance.db.setChestLockData(new ChestLockData(chest.getLocation(), owner, locked));
        if (dbRes < 0) {
            result.databaseError = true;
            result.successfullyUpdated = false;
            return result;
        }
        // Check for double chest and update
        Block adjacentChest = null;
        if (chest.getRelative(BlockFace.EAST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.EAST);
        else if (chest.getRelative(BlockFace.WEST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.WEST);
        else if (chest.getRelative(BlockFace.NORTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.NORTH);
        else if (chest.getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.SOUTH);
        if (adjacentChest != null) {
            int doubleChestDbRes = LuvstarPlugin.instance.db
                    .setChestLockData(new ChestLockData(adjacentChest.getLocation(), owner, locked));
            if (doubleChestDbRes < 0) {
                result.databaseError = true;
                result.successfullyUpdated = false;
                return result;
            }
            result.isDoubleChest = true;
            result.doubleChestLoc = adjacentChest.getLocation();
        }
        result.owner = owner;
        result.playerIsNotOwner = false;
        result.successfullyUpdated = true;
        return result;
    }

    public static ChestLockUpdateResult removeChestlock(Block chest, String owner, boolean ignoreDifferentOwner) {
        ChestLockUpdateResult result = new ChestLockUpdateResult(chest.getLocation());
        ChestLockData existingData = LuvstarPlugin.instance.db.getChestLockData(chest.getLocation());
        if (existingData == null) {
            result.isAlreadyInThisState = true;
            result.successfullyUpdated = false;
            return result;
        }
        if (!existingData.owner.equalsIgnoreCase(owner) && !ignoreDifferentOwner) {
            result.successfullyUpdated = false;
            result.playerIsNotOwner = true;
            result.owner = existingData.owner;
            return result;
        }
        int dbDeleteRes = LuvstarPlugin.instance.db.deleteChestLockData(chest.getLocation());
        if (dbDeleteRes < 0) {
            result.successfullyUpdated = false;
            result.databaseError = true;
            result.owner = owner;
            return result;
        }
        // Check for double chest and update
        Block adjacentChest = null;
        if (chest.getRelative(BlockFace.EAST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.EAST);
        else if (chest.getRelative(BlockFace.WEST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.WEST);
        else if (chest.getRelative(BlockFace.NORTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.NORTH);
        else if (chest.getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.SOUTH);
        if (adjacentChest != null) {
            int doubleChestDbRes = LuvstarPlugin.instance.db.deleteChestLockData(adjacentChest.getLocation());
            if (doubleChestDbRes < 0) {
                result.databaseError = true;
                result.successfullyUpdated = false;
                return result;
            }
            result.isDoubleChest = true;
            result.doubleChestLoc = adjacentChest.getLocation();
        }
        result.successfullyUpdated = true;
        result.owner = null;
        return result;
    }

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
                return 3;
        }
    }
}
