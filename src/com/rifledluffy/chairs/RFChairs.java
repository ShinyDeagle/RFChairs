package com.rifledluffy.chairs;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.command.CommandManager;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.managers.WorldGuardManager;
import com.rifledluffy.chairs.metrics.MetricsLite;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RFChairs extends JavaPlugin {
	
	private static RFChairs instance;
	public CommandManager commandManager;
	public ConfigManager cfgManager;
	public ChairManager chairManager;
	public MessageManager messageManager;
	public WorldGuardManager worldGuardManager;

	@Override
	public void onLoad() {
		setInstance(this);
		loadWorldGuard();
	}

	private void loadWorldGuard() {
		try {
			Class.forName("com.sk89q.worldguard.WorldGuard");
			Class.forName("com.sk89q.worldedit.WorldEdit");
			Class.forName("com.sk89q.worldedit.math.BlockVector3");
			worldGuardManager = new WorldGuardManager();
			worldGuardManager.setup();
			getLogger().info("Found WorldGuard && WorldEdit! Applying Custom Flag...");
		} catch (ClassNotFoundException e) {
			getLogger().info("Missing either WorldGuard or WorldEdit! Disabling Custom Flag Features...");
			getLogger().info("Latest WorldGuard/WorldEdit features could be missing. Please Update!");
		}

		if (worldGuardManager != null) worldGuardManager.register();
	}
	
	@Override
	public void onEnable() {
		
		@SuppressWarnings("unused")
		MetricsLite metrics = new MetricsLite(this);
		
        commandManager = new CommandManager();
        commandManager.setup();
        
		loadConfigManager();
		
		chairManager = new ChairManager();
		chairManager.clearFakeSeatsFromFile(this);
		chairManager.loadToggled();
		
		messageManager = new MessageManager();
		messageManager.loadMuted();
		
		BlockFilter.reload();
		chairManager.reload(this);
		messageManager.reload(this);
		getServer().getPluginManager().registerEvents(chairManager, this);
		getServer().getPluginManager().registerEvents(messageManager, this);
		
		getLogger().info("Rifle's Chairs has been enabled!");		
	}
	
	@Override
	public void onDisable() {
		chairManager.saveToggled();
		messageManager.saveMuted();
		
		chairManager.shutdown();
		
		getLogger().info("Saving Configuration Files!");
		cfgManager.saveData();

		Bukkit.getOnlinePlayers().forEach(p -> {
					PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
					if (regen == null) return;
					if (regen.getDuration() > 1000) p.removePotionEffect(PotionEffectType.REGENERATION);
				});
		
		getLogger().info("Rifle's Chairs has been disabled!");
	}
	
	public void loadConfigManager() {
		cfgManager = new ConfigManager();
		try {
			cfgManager.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cfgManager.reloadConfig();
	}
	
	public ConfigManager getConfigManager() {
		return cfgManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.cfgManager = configManager;
	}
	
	public static RFChairs getInstance() {
        return instance;
    }
	
	public WorldGuardManager getWorldGuardManager() {
		return this.worldGuardManager;
	}

	private static void setInstance(RFChairs instance) {
    	RFChairs.instance = instance;
    }

	public void log(String string) {
      getLogger().info(string);
    }

	boolean hasWorldGuard() {
		return worldGuardManager != null;
	}

}
