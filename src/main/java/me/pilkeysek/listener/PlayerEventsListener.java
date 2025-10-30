package me.pilkeysek.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.data.ChestLockData;
import me.pilkeysek.locking.SignUtil;

public class PlayerEventsListener extends PlayerListener {
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock().getType() == Material.CHEST) {
                if(event.getPlayer().getItemInHand().getType() == Material.SIGN) {
                    BlockFace blockFace = event.getBlockFace();
                    if(blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH || blockFace == BlockFace.WEST || blockFace == BlockFace.EAST) {
                        Block block = event.getClickedBlock();
                        Block relativeBlock = block.getRelative(blockFace);
                        if(relativeBlock.getType() != Material.AIR) return;
                        // Cancel event to prevent opening chest and instead place sign
                        event.setCancelled(true);
                        relativeBlock.setType(Material.WALL_SIGN);
                        relativeBlock.setData(SignUtil.blockFaceToSignRotationData(blockFace));
                        Sign sign = (Sign) relativeBlock.getState();
                        sign.setLine(0, "[private]");
                        if(event.getPlayer().getItemInHand().getAmount() != 1) {
                            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                        } else {
                            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        }
                    }
                } else {
                    ChestLockData data = LuvstarPlugin.instance.db.getChestLockData(event.getClickedBlock().getLocation());
                    if(data == null) return;
                    if(data.canDoThingsWith(event.getPlayer())) return;
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + data.owner + ChatColor.RED + ". You can't open it.");
                }
            }
        }
    }
}
