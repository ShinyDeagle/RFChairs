package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand extends SubCommand {
	
	private RFChairs plugin = RFChairs.getInstance();
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			onCommand((Player) sender, args);
		} else if (sender instanceof ConsoleCommandSender) {
			onCommand((ConsoleCommandSender) sender, args);
		} else return;
	}
    
    @Override
	public void onCommand(ConsoleCommandSender sender, String[] args) {
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
    public void onCommand(Player player, String[] args) {
    	player.sendMessage(ChatColor.YELLOW + "--------------" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Available Commands" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + "--------------");
    	player.sendMessage(ChatColor.GOLD + "/rfc or /rfchairs" + ChatColor.GRAY + " is the main command");
    	player.sendMessage(ChatColor.GOLD + "/rfchairs reload" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Reloads the config and messages");
    	player.sendMessage(ChatColor.GOLD + "/rfchairs reset" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Resets all chairs");
    	player.sendMessage(ChatColor.GOLD + "/rfchairs toggle" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Disables seating on chairs for the executor");
    	player.sendMessage(ChatColor.GOLD + "/rfchairs mute" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Mutes event messages from the plugin for the executor");
    	player.sendMessage(ChatColor.GOLD + "/rfchairs update" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Runs a check on their current version");
    	player.sendMessage(ChatColor.YELLOW + "------------------------------------------------");
    }

    @Override
    public String name() {
        return plugin.commandManager.help;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}