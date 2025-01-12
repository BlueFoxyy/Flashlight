package me.foxyy;

import me.foxyy.commands.*;
import me.foxyy.events.EventListener;
import me.foxyy.tasks.UpdateLightTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Flashlight extends JavaPlugin {
    private static Flashlight instance;

    FileConfiguration config = getConfig();

    public static Flashlight getInstance() {
        return instance;
    }

    public Map<Player, Boolean> flashlightState = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        // register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // load config
        loadConfig();

        // register commands
        registerCommands();

        // register tasks
        registerTasks();

        // register all online users
        for (Player player : getServer().getOnlinePlayers()) {
            flashlightState.put(player, false);
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        UpdateLightTask.clear();
        flashlightState.clear();
    }

    public FileConfiguration getMainConfig() {
        return this.config;
    }

    private void loadConfig() {
        config.addDefault("degree", 45);
        config.addDefault("depth", 30);
        config.addDefault("brightness", 10);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("flashlight")).setExecutor(new FlashlightCommand());
        Objects.requireNonNull(this.getCommand("config")).setExecutor(new FlashlightConfigCommand());
    }

    private void registerTasks() {
        new UpdateLightTask().runTaskTimer(this, 1, 1);
    }
}

