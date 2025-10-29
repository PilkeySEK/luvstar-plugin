package me.pilkeysek.data;

import org.bukkit.Location;

public class ChestLockData {
    public String owner;
    public boolean locked;
    public Location loc;
    public ChestLockData(Location loc, String owner, boolean locked) {
        this.owner = owner;
        this.locked = locked;
        this.loc = loc;
    }
    public String getWorldName() {
        return loc.getWorld().getName();
    }
}
