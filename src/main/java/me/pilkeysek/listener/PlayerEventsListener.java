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
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock().getType() == Material.CHEST) {
                ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(event.getClickedBlock().getLocation());
                if(data == null) return;
                if(data.canDoThingsWith(event.getPlayer())) return;
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + data.owner + ChatColor.RED + ". You can't open it.");
            }
        }
    }
}
