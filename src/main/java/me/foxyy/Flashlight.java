package me.foxyy;

import me.foxyy.commands.*;
import me.foxyy.events.EventListener;
import me.foxyy.tasks.UpdateLightTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Flashlight extends JavaPlugin {
    private static Flashlight instance;

    FileConfiguration config = getConfig();
    private final static Map<String, Object> defaultConfiguration;
    static {
        defaultConfiguration = new HashMap<>();
        defaultConfiguration.put("brightness", 10);
        defaultConfiguration.put("degree", 45);
        defaultConfiguration.put("depth", 30);
        defaultConfiguration.put("toggleState", false);
    }

    public static Flashlight getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // save config
        saveConfig();

        // register commands
        registerCommands();

        // register tasks
        registerTasks();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        UpdateLightTask.clear();
        saveConfig();
    }

    public ConfigurationSection getPlayerConfig(UUID uuid) {
        ConfigurationSection configurationSection = this.config.getConfigurationSection(uuid.toString());
        if (configurationSection == null) {
            this.config.createSection(uuid.toString(), defaultConfiguration);
            return this.config.getConfigurationSection(uuid.toString());
        }
        return configurationSection;
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("flashlight")).setExecutor(new FlashlightCommand());
        Objects.requireNonNull(this.getCommand("config")).setExecutor(new FlashlightConfigCommand());
    }

    private void registerTasks() {
        new UpdateLightTask().runTaskTimer(this, 1, 1);
    }
}

