package com.rifledluffy.chairs;

import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.command.CommandManager;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.dependencymanagers.WorldGuardManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class RFChairs extends JavaPlugin {
    private static RFChairs instance;
    private CommandManager commandManager;
    private ConfigManager cfgManager;
    private ChairManager chairManager;
    private MessageManager messageManager;
    private WorldGuardManager worldGuardManager;

    public static @NotNull RFChairs getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        loadWorldGuard();
    }

    private void loadWorldGuard() {
        try {
            worldGuardManager = new WorldGuardManager(this);
            getComponentLogger().info("Found WorldGuard & WorldEdit! Applying Custom Flag...");
        } catch (NoClassDefFoundError e) {
            getComponentLogger().info("Missing either WorldGuard or WorldEdit! Disabling Custom Flag Features...");
            getComponentLogger().info("Latest WorldGuard features could be missing. Please Update!", e);
        }

        if (worldGuardManager != null) {
            worldGuardManager.register();
        }
    }

    @Override
    public void onEnable() {
        new Metrics(this, 2979);

        commandManager = new CommandManager();
        commandManager.setup();

        loadConfigManager();

        chairManager = new ChairManager(this);
        chairManager.clearFakeSeatsFromFile();
        chairManager.loadToggled();

        messageManager = new MessageManager();
        messageManager.loadMuted();

        BlockFilter.reload();
        chairManager.reload();
        messageManager.reload();
        getServer().getPluginManager().registerEvents(chairManager, this);
        getServer().getPluginManager().registerEvents(messageManager, this);

        getComponentLogger().info("Rifle's Chairs has been enabled!");
    }

    @Override
    public void onDisable() {
        chairManager.saveToggled();
        messageManager.saveMuted();

        chairManager.shutdown();

        getComponentLogger().info("Saving Configuration Files!");
        cfgManager.saveData();

        Bukkit.getOnlinePlayers().forEach(p -> { // todo just clearing regen from everyone is NOT right. should only clear sitting players
            PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
            if (regen == null) return;
            if (regen.getDuration() > 1000) p.removePotionEffect(PotionEffectType.REGENERATION);
        });

        getComponentLogger().info("Rifle's Chairs has been disabled!");
    }

    public void loadConfigManager() {
        cfgManager = new ConfigManager();
        cfgManager.setup();
        cfgManager.reloadConfig();
    }

    public ConfigManager getConfigManager() {
        return cfgManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.cfgManager = configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ChairManager getChairManager() {
        return chairManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public WorldGuardManager getWorldGuardManager() {
        return this.worldGuardManager;
    }

    boolean hasWorldGuard() {
        return worldGuardManager != null;
    }
}
