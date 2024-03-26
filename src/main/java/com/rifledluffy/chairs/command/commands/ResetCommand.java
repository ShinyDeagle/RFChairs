package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.RFChairs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public class ResetCommand implements SubCommand {
    private final RFChairs plugin = RFChairs.getInstance();

    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        plugin.getChairManager().clearFakeSeats();
        plugin.getChairManager().clearFakeSeatsFromFile(plugin);
        plugin.getLogger().info("Chairs Reset!");
        sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Chairs Reset");
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "reset";
    }

    @Override
    public @NotNull String info() {
        return "Resets all chairs";
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