package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.messages.MessagePath;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResetCommand implements SubCommand {
    private final RFChairs plugin = RFChairs.getInstance();

    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        plugin.getChairManager().clearFakeSeats();
        plugin.getChairManager().clearFakeSeatsFromFile(plugin);
        plugin.getLogger().info("Chairs Reset!");

        plugin.getMessageManager().sendLang(sender, MessagePath.COMMAND_RESET_SUCCESS);
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "reset";
    }

    @Override
    public @NotNull Component info() {
        return plugin.getMessageManager().getLang(MessagePath.COMMAND_RESET_INFO);
    }

    @Override
    public @NotNull String @NotNull [] aliases() {
        return new String[0];
    }

    @Override
    public boolean needsPlayer() {
        return false;
    }

    @Override
    public boolean checkPermission(@NotNull Permissible permissible) {
        return permissible.hasPermission("rfchairs.reset") || permissible.hasPermission("rfchairs.manage");
    }
}