package me.pilkeysek.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.pilkeysek.LuvstarPlugin;
import me.pilkeysek.Util;
import me.pilkeysek.data.ChestLockUpdateResult;

public class ForceRelinquishCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this.");
                return true;
            }
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You must be op to use this.");
                return true;
            }
            Block targetBlock = player.getTargetBlock(null, 5);
            if (targetBlock.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "Too far away.");
                return true;
            }
            if (targetBlock.getType() != Material.CHEST) {
                player.sendMessage(ChatColor.RED + "Only chests can be locked.");
                return true;
            }
            ChestLockUpdateResult res = Util.removeChestlock(targetBlock, player.getName(), true);
            if (res.successfullyUpdated) {
                if (res.isDoubleChest) {
                    player.sendMessage(ChatColor.GREEN + "Forcibly relinquished ownership of the double chest at "
                            + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + " and " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.doubleChestLoc) + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(
                            ChatColor.GREEN + "Forcibly relinquished ownership of the chest at " + ChatColor.DARK_AQUA
                                    + Util.locToIntString(res.loc) + ChatColor.GREEN + ".");
                }
                LuvstarPlugin.instance.logInfo("Player " + player.getName() + " forcibly relinquished ownership of a chest at "
                        + Util.locToIntString(res.loc));
            } else {
                if (res.databaseError) {
                    player.sendMessage(ChatColor.RED + "A database error occurred.");
                }
            }
            return true;
    }
    
}
