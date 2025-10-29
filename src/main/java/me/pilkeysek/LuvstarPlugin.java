package me.pilkeysek;

import me.pilkeysek.listener.BlockEventsListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
            pluginConfig.getString("postgres_password")
        );
        getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, new BlockEventsListener(), Priority.Highest, this);
        getServer().getLogger().info("Luvstar Plugin initialized");
    }
    @Override
    public void onDisable() {
        logInfo("Byeee...");
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
            if(!config_keys.contains(key)) {
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