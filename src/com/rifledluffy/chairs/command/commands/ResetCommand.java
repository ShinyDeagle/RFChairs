package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;

import net.md_5.bungee.api.ChatColor;

public class ResetCommand extends SubCommand {
	
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
		plugin.chairManager.clearFakeSeats();
		plugin.chairManager.clearFakeSeatsFromFile(plugin);
		plugin.getLogger().info("Chairs Reset!");
		sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Chairs Reset");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("rfchairs.reset") && !player.hasPermission("rfchairs.manage")) return;
		plugin.chairManager.clearFakeSeats();
		plugin.chairManager.clearFakeSeatsFromFile(plugin);
		plugin.getLogger().info("Chairs Reset!");
		player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Chairs Reset");
	}

	@Override
	public String name() {
		return plugin.commandManager.reset;
	}

	@Override
	public String info() {
		return "Resets all chairs";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}

}