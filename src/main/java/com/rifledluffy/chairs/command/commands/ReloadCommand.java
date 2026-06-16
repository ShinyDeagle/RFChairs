package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.messages.MessagePath;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand implements SubCommand {
    private final RFChairs plugin = RFChairs.getInstance();

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        ConfigManager newConfig = plugin.getConfigManager();
        newConfig.reloadConfig();
        plugin.setConfigManager(newConfig);
        BlockFilter.reload();
        plugin.getChairManager().reload();
        plugin.getMessageManager().reload();
        plugin.getMessageManager().sendLang(sender, MessagePath.COMMAND_RELOAD_SUCCESS);
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "reload";
    }

    @Override
    public @NotNull Component info() {
        return RFChairs.getInstance().getMessageManager().getLang(MessagePath.COMMAND_RELOAD_INFO);
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

    @Override
    public @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        return List.of();
    }
}
