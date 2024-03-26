package com.rifledluffy.chairs.command.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements SubCommand {

    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) { //todo check permissions!
        sender.sendMessage(ChatColor.YELLOW + "--------------" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Available Commands" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + "--------------");
        sender.sendMessage(ChatColor.GOLD + "/rfc or /rfchairs" + ChatColor.GRAY + " is the main command");
        sender.sendMessage(ChatColor.GOLD + "/rfchairs reload" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Reloads the config and messages [Console Can Cast]");
        sender.sendMessage(ChatColor.GOLD + "/rfchairs reset" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Resets all chairs [Console Can Cast]");
        sender.sendMessage(ChatColor.GOLD + "/rfchairs toggle" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Disables seating on chairs for the executor [Player Only]");
        sender.sendMessage(ChatColor.GOLD + "/rfchairs mute" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Mutes event messages from the plugin for the executor [Player Only]");
        sender.sendMessage(ChatColor.GOLD + "/rfchairs update" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Runs a check on their current version [Console Can Cast]");
        sender.sendMessage(ChatColor.YELLOW + "------------------------------------------------");
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "help";
    }

    @Override
    public @NotNull String info() {
        return "";
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
        return true;
    }
}