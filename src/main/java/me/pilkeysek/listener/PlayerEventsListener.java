package me.pilkeysek.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.data.ChestLockData;

public class PlayerEventsListener extends PlayerListener {
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        LuvstarPlugin.instance.logInfo("Event");
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock().getType() == Material.CHEST) {
                ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(event.getClickedBlock().getLocation());
                LuvstarPlugin.instance.logInfo("DATA: " + data);
                // If noone owns this chest, allow opening it
                if(data == null) return;
                // If the chest isn't locked, allow opening it
                if(data.locked == false) return;
                // If the player owns this chest, allow opening it
                if(data.owner.equals(event.getPlayer().getName())) return;
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This chest is locked.");
            }
        }
    }
}
