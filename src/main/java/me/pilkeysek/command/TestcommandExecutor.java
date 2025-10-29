package me.pilkeysek.command;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestcommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Block b = p.getTargetBlock(null, 5);
        p.sendMessage(Integer.toString(b.getData(), 16));
        return true;
    }
}
