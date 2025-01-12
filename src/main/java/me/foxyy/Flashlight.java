package me.foxyy;

import me.foxyy.commands.*;
import me.foxyy.events.EventListener;
import me.foxyy.tasks.UpdateLightTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Flashlight extends JavaPlugin {
    private static Flashlight instance;

    FileConfiguration config = getConfig();

    public static Flashlight getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // load config
        loadConfig();

        // register commands
        registerCommands();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
    }

    public void reload() {
        this.getLogger().info("Reloading the plugin...");
        this.getServer().getScheduler().cancelTasks(this);
        this.reloadConfig();
        this.registerTasks();
        this.getLogger().info("Plugin reloaded.");
    }

    public FileConfiguration getMainConfig() {
        return this.config;
    }

    private void loadConfig() {
        config.addDefault("degree", 15);
        config.addDefault("depth", 30);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("flashlight")).setExecutor(new FlashlightCommand());
        Objects.requireNonNull(this.getCommand("flashlightconfig")).setExecutor(new FlashlightConfigCommand());
    }

    private void registerTasks() {
        new UpdateLightTask().runTaskTimerAsynchronously(this, 1, 1);
    }
}

