package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.config.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements SubCommand {
    private final RFChairs plugin = RFChairs.getInstance();

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        ConfigManager newConfig = plugin.getConfigManager();
        newConfig.reloadConfig();
        newConfig.reloadMessages();
        plugin.setConfigManager(newConfig);
        BlockFilter.reload();
        plugin.getChairManager().reload(plugin);
        plugin.getMessageManager().reload(plugin);
        sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Config Reloaded");
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "reload";
    }

    @Override
    public @NotNull String info() {
        return "Reloads the config and messages";
    }

    @Override
    public String @NotNull [] aliases() {
        return new String[0];
    }

    @Override
    public boolean needsPlayer() {
        return false;
    }

    @Override
    public boolean checkPermission(@NotNull Permissible permissible) {
        return permissible.hasPermission("rfchairs.reload") || permissible.hasPermission("rfchairs.manage");
    }
}
