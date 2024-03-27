package com.rifledluffy.chairs.config;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final JavaPlugin plugin = RFChairs.getPlugin(RFChairs.class);
    private FileConfiguration config;
    /*
     * Configuration Files
     */
    private File configFile;
    private FileConfiguration fake;
    private File fakeFile;

    public void setup() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        fakeFile = new File(plugin.getDataFolder(), "fakes.yml");

        if (!fakeFile.exists()) {
            try {
                fakeFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().info("[Rifle's Chairs] Could not create fakes.yml!");
            }
        }

        fake = YamlConfiguration.loadConfiguration(fakeFile);
    }

    public FileConfiguration getData() {
        return fake;
    }

    public void saveData() {
        try {
            fake.save(fakeFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("[Rifle's Chairs] Could not save fakes.yml!");
        }
    }

    public void reloadData() {
        fake = YamlConfiguration.loadConfiguration(fakeFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().info("[Rifle's Chairs] Could not save config.yml!");
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}