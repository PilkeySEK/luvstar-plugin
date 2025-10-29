package me.pilkeysek.data;

import org.bukkit.Location;

public class ChestLockUpdateResult {
    public boolean databaseError = false;
    public boolean successfullyUpdated = false;
    public boolean playerIsNotOwner = false;
    public String owner = null;
    public Location loc;
    public boolean isDoubleChest = false;
    public Location doubleChestLoc = null;
    public boolean isAlreadyInThisState = false;

    public ChestLockUpdateResult(Location loc) {
        this.loc = loc;
    }
}
