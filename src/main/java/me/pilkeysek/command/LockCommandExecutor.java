package me.pilkeysek.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.pilkeysek.Util;
import me.pilkeysek.data.ChestLockUpdateResult;

public class LockCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this.");
                return true;
            }
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock(null, 5);
            if (targetBlock.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "Too far away.");
                return true;
            }
            if (targetBlock.getType() != Material.CHEST) {
                player.sendMessage(ChatColor.RED + "Only chests can be locked.");
                return true;
            }
            ChestLockUpdateResult res = Util.updateChestLock(targetBlock, player.getName(), true, false);
            if (res.successfullyUpdated) {
                if (res.isDoubleChest) {
                    player.sendMessage(ChatColor.GREEN + "Locked the double chest at " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + " and " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.doubleChestLoc) + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Locked the chest at " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + ".");
                }
            } else {
                if (res.databaseError) {
                    player.sendMessage(ChatColor.RED + "A database error occurred.");
                } else if (res.playerIsNotOwner) {
                    player.sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + res.owner
                            + ChatColor.RED + ". You can't lock it.");
                } else if (res.isAlreadyInThisState) {
                    player.sendMessage(ChatColor.RED + "This chest is already locked.");
                }
            }
            return true;
    }
    
}
