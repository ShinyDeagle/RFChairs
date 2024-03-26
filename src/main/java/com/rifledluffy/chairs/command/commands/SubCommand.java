package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * /<command> <subcommand> args[0] args[1]
 */
public interface SubCommand {

    void onCommand(@NotNull CommandSender sender, @NotNull String[] args);

    void onPlayerCommand(@NotNull Player player, @NotNull String[] args);

    @NotNull String name();

    @NotNull String info();

    @NotNull String @NotNull [] aliases();

    boolean needsPlayer();

    boolean checkPermission(@NotNull Permissible permissible);
}
