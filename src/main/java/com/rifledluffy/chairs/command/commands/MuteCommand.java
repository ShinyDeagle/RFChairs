package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.MessageManager;
import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.utility.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MuteCommand implements SubCommand {

    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        MessageManager messageManager = RFChairs.getInstance().getMessageManager();
        List<UUID> muted = messageManager.muted;
        String message;
        if (muted.contains(player.getUniqueId())) {
            String string = messageManager.messages.getString("mute-message-enabled", "&8[&6Rifle's Chairs&8] &7Event Messaging is now &cDisabled!");
            message = Util.replaceMessage(player, string);
            player.sendMessage(message);
            muted.remove(player.getUniqueId());
        } else {
            String string = messageManager.messages.getString("mute-message-disabled", "&8[&6Rifle's Chairs&8] &7Event Messaging is now &aEnabled!");
            message = Util.replaceMessage(player, string);
            player.sendMessage(message);
            muted.add(player.getUniqueId());
        }
    }

    @Override
    public @NotNull String name() {
        return "mute";
    }

    @Override
    public @NotNull String info() {
        return "";
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
}
