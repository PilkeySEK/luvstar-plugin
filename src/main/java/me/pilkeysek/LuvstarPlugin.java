package me.pilkeysek;

import me.pilkeysek.data.ChestLockData;
import me.pilkeysek.data.ChestLockUpdateResult;
import me.pilkeysek.listener.BlockEventsListener;
import me.pilkeysek.listener.EntityEventsListener;
import me.pilkeysek.listener.PlayerEventsListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        getServer().getPluginManager().registerEvent(Type.ENTITY_EXPLODE, new EntityEventsListener(), Priority.Highest,
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
            ChestLockUpdateResult res = updateChestLock(targetBlock, player.getName(), true, false);
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
            ChestLockUpdateResult res = updateChestLock(targetBlock, player.getName(), false, false);
            if (res.successfullyUpdated) {
                if (res.isDoubleChest) {
                    player.sendMessage(ChatColor.GREEN + "Unlocked the double chest at " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + " and " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.doubleChestLoc) + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Unlocked the chest at " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + ".");
                }
            } else {
                if (res.databaseError) {
                    player.sendMessage(ChatColor.RED + "A database error occurred.");
                } else if (res.playerIsNotOwner) {
                    player.sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + res.owner
                            + ChatColor.RED + ". You can't unlock it.");
                } else if (res.isAlreadyInThisState) {
                    player.sendMessage(ChatColor.RED + "This chest is already unlocked.");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("relinquish")) {
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
            ChestLockUpdateResult res = removeChestlock(targetBlock, player.getName(), false);
            if (res.successfullyUpdated) {
                if (res.isDoubleChest) {
                    player.sendMessage(
                            ChatColor.GREEN + "Relinquished ownership of the double chest at " + ChatColor.DARK_AQUA
                                    + Util.locToIntString(res.loc) + ChatColor.GREEN + " and " + ChatColor.DARK_AQUA
                                    + Util.locToIntString(res.doubleChestLoc) + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Relinquished ownership of the chest at " + ChatColor.DARK_AQUA
                            + Util.locToIntString(res.loc) + ChatColor.GREEN + ".");
                }
            } else {
                if (res.databaseError) {
                    player.sendMessage(ChatColor.RED + "A database error occurred.");
                } else if (res.playerIsNotOwner) {
                    player.sendMessage(ChatColor.RED + "This chest is owned by " + ChatColor.DARK_AQUA + res.owner
                            + ChatColor.RED + ". You can't relinquish ownership of it.");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("frelinquish")) {
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
            ChestLockUpdateResult res = removeChestlock(targetBlock, player.getName(), true);
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
                logInfo("Player " + player.getName() + " forcibly relinquished ownership of a chest at "
                        + Util.locToIntString(res.loc));
            } else {
                if (res.databaseError) {
                    player.sendMessage(ChatColor.RED + "A database error occurred.");
                }
            }
            return true;
        }
        return false;
    }

    private ChestLockUpdateResult updateChestLock(Block chest, String owner, boolean locked,
            boolean ignoreDifferentOwner) {
        ChestLockUpdateResult result = new ChestLockUpdateResult(chest.getLocation());
        ChestLockData existingData = db.getChestLockData(chest.getLocation());
        if (!ignoreDifferentOwner && existingData != null && !existingData.owner.equalsIgnoreCase(owner)) {
            result.successfullyUpdated = false;
            result.playerIsNotOwner = true;
            result.owner = existingData.owner;
            return result;
        }
        if (existingData != null && existingData.owner.equals(owner) && existingData.locked == locked) {
            result.successfullyUpdated = false;
            result.isAlreadyInThisState = true;
            return result;
        }
        int dbRes = db.setChestLockData(new ChestLockData(chest.getLocation(), owner, locked));
        if (dbRes < 0) {
            result.databaseError = true;
            result.successfullyUpdated = false;
            return result;
        }
        // Check for double chest and update
        Block adjacentChest = null;
        if (chest.getRelative(BlockFace.EAST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.EAST);
        else if (chest.getRelative(BlockFace.WEST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.WEST);
        else if (chest.getRelative(BlockFace.NORTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.NORTH);
        else if (chest.getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.SOUTH);
        if (adjacentChest != null) {
            int doubleChestDbRes = db.setChestLockData(new ChestLockData(adjacentChest.getLocation(), owner, locked));
            if (doubleChestDbRes < 0) {
                result.databaseError = true;
                result.successfullyUpdated = false;
                return result;
            }
            result.isDoubleChest = true;
            result.doubleChestLoc = adjacentChest.getLocation();
        }
        result.owner = owner;
        result.playerIsNotOwner = false;
        result.successfullyUpdated = true;
        return result;
    }

    public ChestLockUpdateResult removeChestlock(Block chest, String owner, boolean ignoreDifferentOwner) {
        ChestLockUpdateResult result = new ChestLockUpdateResult(chest.getLocation());
        ChestLockData existingData = db.getChestLockData(chest.getLocation());
        if (existingData == null) {
            result.isAlreadyInThisState = true;
            result.successfullyUpdated = false;
            return result;
        }
        if (!existingData.owner.equalsIgnoreCase(owner) && !ignoreDifferentOwner) {
            result.successfullyUpdated = false;
            result.playerIsNotOwner = true;
            result.owner = existingData.owner;
            return result;
        }
        int dbDeleteRes = db.deleteChestLockData(chest.getLocation());
        if (dbDeleteRes < 0) {
            result.successfullyUpdated = false;
            result.databaseError = true;
            result.owner = owner;
            return result;
        }
        // Check for double chest and update
        Block adjacentChest = null;
        if (chest.getRelative(BlockFace.EAST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.EAST);
        else if (chest.getRelative(BlockFace.WEST).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.WEST);
        else if (chest.getRelative(BlockFace.NORTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.NORTH);
        else if (chest.getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            adjacentChest = chest.getRelative(BlockFace.SOUTH);
        if (adjacentChest != null) {
            int doubleChestDbRes = db.deleteChestLockData(adjacentChest.getLocation());
            if (doubleChestDbRes < 0) {
                result.databaseError = true;
                result.successfullyUpdated = false;
                return result;
            }
            result.isDoubleChest = true;
            result.doubleChestLoc = adjacentChest.getLocation();
        }
        result.successfullyUpdated = true;
        result.owner = null;
        return result;
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