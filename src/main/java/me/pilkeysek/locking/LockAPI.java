package me.pilkeysek.locking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.data.ChestLockData;

public class LockAPI {
    public static final BlockFace[] possibleSignFaces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    public boolean isLocked(Block block) {
        if(block.getType() == Material.CHEST) {
            for(BlockFace face : possibleSignFaces) {
                Block relative = block.getRelative(face);
                if(relative.getType() != Material.WALL_SIGN) continue;
                BlockState signState = relative.getState();
                // should always be the case
                if(!(signState instanceof Sign)) continue;
                
            }
        }
        ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(block.getLocation());
        if(data == null) return false;
        return data.locked;
    }
}
