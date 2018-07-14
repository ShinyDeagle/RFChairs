package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    /*
    /<command> <subcommand> args[0] args[1]
     */

    public SubCommand() {}
    
    public abstract void onCommand(CommandSender sender, String[] args);
    
    public abstract void onCommand(ConsoleCommandSender sender, String[] args);

    public abstract void onCommand(Player player, String[] args);

    public abstract String name();

    public abstract String info();

    public abstract String[] aliases();
}
