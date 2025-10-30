package me.pilkeysek.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.Util;
import me.pilkeysek.data.ChestLockData;
import me.pilkeysek.locking.SignUtil;

public class BlockEventsListener extends BlockListener {

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() == Material.CHEST) {
            ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(event.getBlock().getLocation());
            
            if(data == null) return;
            if(data.canDoThingsWith(event.getPlayer())) {
                // remove from db when chest is destroyed
                Util.removeChestlock(event.getBlock(), event.getPlayer().getName(), true);
                return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + data.owner + ChatColor.RED + ". You can't break it.");
        }
    }

    // Event places signs that are on the ground onto chests
    @Override
    public void onSignChange(SignChangeEvent event) {
        // tbf i dont know why im adding this but maybe it can help with compatibility with other plugins
        // the event priority is set to HIGHEST anyway
        if(event.isCancelled()) return;
        String[] lines = event.getLines();
        if(!SignUtil.isLockSign(lines)) return;
        Block block = event.getBlock();
        // we need to backup the blockData here because it seems that it is set to 0 by Block#setType
        byte blockData = block.getData();
        BlockFace face = SignUtil.invertBlockFace(SignUtil.standingSignRotationDataToBlockFace(blockData));
        if(face == BlockFace.SELF) return;
        Block relative = block.getRelative(face);
        if(relative.getType() != Material.CHEST) return; // In the future this should check against more things like doors, etc.
        block.setType(Material.WALL_SIGN);
        byte wallSignData = SignUtil.standingToWallSignData(blockData);
        if(wallSignData == Byte.MAX_VALUE) { // this *should* never happen, but who knows (we are checking face == SELF above already, which should catch it)
            event.getPlayer().sendMessage("how did we get here");
            return;
        }
        block.setData(wallSignData);
        BlockState state = block.getState();
        if(!(state instanceof Sign)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Something went wrong: BlockState is not an instance of Sign");
            return;
        }

        // copy all lines
        int index = 0;
        for(String line : lines) {
            ((Sign) state).setLine(index, line);
            index++;
        }
    }
}
