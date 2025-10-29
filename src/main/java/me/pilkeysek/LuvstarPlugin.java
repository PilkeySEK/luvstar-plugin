package me.pilkeysek;

import me.pilkeysek.data.ChestLockData;
import me.pilkeysek.listener.BlockEventsListener;
import me.pilkeysek.listener.PlayerEventsListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class LuvstarPlugin extends JavaPlugin {
    private Logger logger;
    public LuvstarPluginConfig config;
    public Configuration pluginConfig;
    public DatabaseUtil db;
    public static LuvstarPlugin instance;

    @Override
    public void onEnable() {
        LuvstarPlugin.instance = this;
        this.pluginConfig = getConfiguration();
        this.pluginConfig.load();
        this.logger = getServer().getLogger();
        this.config = new LuvstarPluginConfig();
        defaultPluginConfig();
        this.db = new DatabaseUtil(
                pluginConfig.getString("postgres_db"),
                pluginConfig.getString("postgres_host"),
                pluginConfig.getString("postgres_port"),
                pluginConfig.getString("postgres_user"),
                pluginConfig.getString("postgres_password"));
        getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, new BlockEventsListener(), Priority.Highest,
                this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, new PlayerEventsListener(), Priority.Highest,
                this);
        getServer().getLogger().info("Luvstar Plugin initialized");
    }

    @Override
    public void onDisable() {
        logInfo("Byeee...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lock")) {
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
            ChestLockData data = db.getChestLockData(targetBlock.getLocation());
            if (data == null || data.owner.equals(player.getName())) {
                if (data != null && data.locked == true) {
                    player.sendMessage(ChatColor.RED + "The chest is already locked.");
                    return true;
                }
                int res = db.setChestLockData(new ChestLockData(targetBlock.getLocation(), player.getName(), true));
                if (res >= 0) {
                    if(data == null) {
                        player.sendMessage(ChatColor.AQUA + "Note: The chest was not owned by anyone, you own it now.");
                    }
                    player.sendMessage(ChatColor.GREEN + "Locked the chest at " + (int) targetBlock.getLocation().getX() + " "
                            + (int) targetBlock.getLocation().getY() + " " + (int) targetBlock.getLocation().getZ() + ".");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Something went wrong while trying to update the database.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "This chest is owned by " + data.owner + ". You can't lock it.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("unlock")) {
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
                player.sendMessage(ChatColor.RED + "Only chests can be unlocked.");
                return true;
            }
            ChestLockData data = db.getChestLockData(targetBlock.getLocation());
            if (data == null || data.owner.equals(player.getName())) {
                if (data != null && data.locked == false) {
                    player.sendMessage(ChatColor.RED + "The chest is already unlocked.");
                    return true;
                }
                int res = db.setChestLockData(new ChestLockData(targetBlock.getLocation(), player.getName(), false));
                if (res >= 0) {
                    if(data == null) {
                        player.sendMessage(ChatColor.AQUA + "Note: The chest was not owned by anyone, you own it now.");
                    }
                    player.sendMessage(ChatColor.GREEN + "Unlocked the chest at " + (int) targetBlock.getLocation().getX() + " "
                            + (int) targetBlock.getLocation().getY() + " " + (int) targetBlock.getLocation().getZ() + ".");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Something went wrong while trying to update the database.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "This chest is owned by " + data.owner + ". You can't unlock it.");
                return true;
            }
        }
        return false;
    }

    private void defaultPluginConfig() {
        HashMap<String, String> default_stuff = new HashMap<>();
        default_stuff.put("postgres_host", "localhost");
        default_stuff.put("postgres_port", "5432");
        default_stuff.put("postgres_db", "luvstarplugin");
        default_stuff.put("postgres_user", "postgres");
        default_stuff.put("postgres_password", "password");
        List<String> config_keys = getConfiguration().getKeys();
        default_stuff.forEach((key, value) -> {
            if (!config_keys.contains(key)) {
                pluginConfig.setProperty(key, value);
            }
        });
        pluginConfig.save();
    }

    public void logInfo(String s) {
        this.logger.info("[Luvstar Plugin] " + s);
        this.pluginConfig.save();
    }
}