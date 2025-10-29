package me.pilkeysek.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.Util;
import me.pilkeysek.data.ChestLockData;

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
}
