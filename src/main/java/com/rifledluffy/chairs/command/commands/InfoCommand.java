package com.rifledluffy.chairs.command.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoCommand implements SubCommand { //todo

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {

    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
    }

    @Override
    public @NotNull String name() {
        return "info";
    }

    @Override
    public @NotNull Component info() {
        return Component.empty();
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
        return permissible.hasPermission("rfchairs.info");
    }

    @Override
    public @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        return List.of();
    }
}