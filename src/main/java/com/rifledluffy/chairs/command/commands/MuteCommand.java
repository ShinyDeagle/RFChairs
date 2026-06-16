package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.MessageManager;
import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.messages.MessagePath;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MuteCommand implements SubCommand {

    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
        MessageManager messageManager = RFChairs.getInstance().getMessageManager();
        List<UUID> muted = messageManager.getMuted();
        if (muted.contains(player.getUniqueId())) {
            messageManager.sendLang(player, MessagePath.COMMAND_MUTE_ENABLED);
            muted.remove(player.getUniqueId());
        } else {
            messageManager.sendLang(player, MessagePath.COMMAND_MUTE_DISABLED);
            muted.add(player.getUniqueId());
        }
    }

    @Override
    public @NotNull String name() {
        return "mute";
    }

    @Override
    public @NotNull Component info() {
        return RFChairs.getInstance().getMessageManager().getLang(MessagePath.COMMAND_MUTE_INFO);
    }

    @Override
    public String @NotNull [] aliases() {
        return new String[0];
    }

    @Override
    public boolean needsPlayer() {
        return true;
    }

    @Override
    public boolean checkPermission(@NotNull Permissible permissible) {
        return permissible.hasPermission("rfchairs.mute");
    }

    @Override
    public @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        return List.of();
    }
}
