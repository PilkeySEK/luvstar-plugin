package me.pilkeysek.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    /**
     * Whether the specified player can interact/break the block that this data is associated with
     * @param player The player trying to interact/break this block
     */
    public boolean canDoThingsWith(Player player) {
        if(!this.locked) return true;
        if(this.owner.equals(player.getName())) return true;
        return false;
    }

    @Override
    public String toString() {
        return String.format("ChestLockData(x=%d y=%d z=%d world='%s' owner='%s' locked=%b)", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), loc.getWorld().getName(), owner, locked);
    }
}
