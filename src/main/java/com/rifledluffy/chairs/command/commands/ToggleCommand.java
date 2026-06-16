package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.MessageManager;
import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.messages.MessagePath;
import com.rifledluffy.chairs.messages.PlaceHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ToggleCommand implements SubCommand {

    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
        MessageManager messageManager = RFChairs.getInstance().getMessageManager();
        List<UUID> toggled = RFChairs.getInstance().getChairManager().getToggled();

        if (toggled.contains(player.getUniqueId())) {
            messageManager.sendLang(player, MessagePath.COMMAND_TOGGLE_ENABLED,
                    Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), player.displayName()));
            toggled.remove(player.getUniqueId());
        } else {
            messageManager.sendLang(player, MessagePath.COMMAND_TOGGLE_DISABLED,
                    Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), player.displayName()));
            toggled.add(player.getUniqueId());
        }
    }

    @Override
    public @NotNull String name() {
        return "toggle";
    }

    @Override
    public @NotNull Component info() {
        return RFChairs.getInstance().getMessageManager().getLang(MessagePath.COMMAND_TOGGLE_INFO);
    }

    @Override
    public @NotNull String @NotNull [] aliases() {
        return new String[0];
    }

    @Override
    public boolean needsPlayer() {
        return true;
    }

    @Override
    public boolean checkPermission(@NotNull Permissible permissible) {
        return permissible.hasPermission("rfchairs.toggle");
    }

    @Override
    public @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        return List.of();
    }
}
