package me.pilkeysek.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.data.ChestLockData;

public class BlockEventsListener extends BlockListener {

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(event.getBlock().getLocation());
        if(data == null) {
            int res = LuvstarPlugin.instance.db.setChestLockData(new ChestLockData(event.getBlock().getLocation(), event.getPlayer().getName(), false));
            LuvstarPlugin.instance.logInfo("RES: " + res);
        } else {
            LuvstarPlugin.instance.logInfo("OWNER: " + data.owner + ", LOCKED: " + data.locked);
        }
            // plugin.logger.info(event.getBlock().getType().toString());
        event.getPlayer().sendMessage(ChatColor.RED + "Hey you freak! You can't break that :3");
        event.setCancelled(true);
    }
}
