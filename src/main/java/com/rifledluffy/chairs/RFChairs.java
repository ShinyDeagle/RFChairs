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

    private static void setInstance(@NotNull RFChairs instance) {
        RFChairs.instance = instance;
    }

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

        if (worldGuardManager != null) {
            worldGuardManager.register();
        }
    }

    @Override
    public void onEnable() {
        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, 2979);

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
        messageManager.reload();
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

        Bukkit.getOnlinePlayers().forEach(p -> { // todo just clearing regen from everyone is NOT right. should only clear sitting players
            PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
            if (regen == null) return;
            if (regen.getDuration() > 1000) p.removePotionEffect(PotionEffectType.REGENERATION);
        });

        getLogger().info("Rifle's Chairs has been disabled!");
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
