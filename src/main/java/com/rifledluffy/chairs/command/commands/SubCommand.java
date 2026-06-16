package com.rifledluffy.chairs.command.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * /<command> <subcommand> args[0] args[1]
 */
public interface SubCommand {

    void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args);

    void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args);

    @NotNull String name();

    @NotNull Component info();

    @NotNull String @NotNull [] aliases(); // funny thing: No subcommand defines an alias at time of writing this

    boolean needsPlayer();

    boolean checkPermission(@NotNull Permissible permissible);

    @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args);
}
