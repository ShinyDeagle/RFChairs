package com.rifledluffy.chairs.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.rifledluffy.chairs.RFChairs;

public class ConfigManager {

	private JavaPlugin plugin = RFChairs.getPlugin(RFChairs.class);
	
	/*
	 * Configuration Files
	 */
	
	FileConfiguration config;
    File configFile;
   
    FileConfiguration fake;
    File fakeFile;
    
    FileConfiguration message;
    File messageFile;
   
    public void setup() throws IOException {
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
                    }
                    catch (IOException e) {
                            Bukkit.getServer().getLogger().info("[Rifle's Chairs] Could not create fakes.yml!");
                    }
            }
            
            messageFile = new File(plugin.getDataFolder(), "messages.yml");
            
            if (!messageFile.exists()) {
            	messageFile.getParentFile().mkdirs();
            	plugin.saveResource("messages.yml", false);
            }
           
            fake = YamlConfiguration.loadConfiguration(fakeFile);
            message = YamlConfiguration.loadConfiguration(messageFile);
    }
   
    public FileConfiguration getData() {
            return fake;
    }
   
    public void saveData() {
            try {
                    fake.save(fakeFile);
            }
            catch (IOException e) {
                    Bukkit.getServer().getLogger().info("[Rifle's Chairs] Could not save fakes.yml!");
            }
    }
   
    public void reloadData() {
            fake = YamlConfiguration.loadConfiguration(fakeFile);
    }
    
    public FileConfiguration getMessages() {
        return message;
	}
	
	public void saveMessages() {
	        try {
	                message.save(messageFile);
	        }
	        catch (IOException e) {
	        		plugin.getLogger().info("[Rifle's Chairs] Could not save messages.yml!");
	        }
	}
	
	public void reloadMessages() {
	        message = YamlConfiguration.loadConfiguration(messageFile);
	}
   
    public FileConfiguration getConfig() {
            return config;
    }
   
    public void saveConfig() {
            try {
                    config.save(configFile);
            }
            catch (IOException e) {
                    plugin.getLogger().info("[Rifle's Chairs] Could not save config.yml!");
            }
    }
   
    public void reloadConfig() {
            config = YamlConfiguration.loadConfiguration(configFile);
    }
   
    public PluginDescriptionFile getDesc() {
            return plugin.getDescription();
    }
}