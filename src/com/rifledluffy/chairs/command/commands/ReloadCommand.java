package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.config.ConfigManager;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand extends SubCommand {
	
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
		ConfigManager newConfig = plugin.getConfigManager();
		newConfig.reloadConfig();
		newConfig.reloadMessages();
		plugin.setConfigManager(newConfig);
		plugin.chairManager.reload(plugin);
		plugin.messageManager.reload(plugin);
		sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Config Reloaded");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("rfchairs.reload") && !player.hasPermission("rfchairs.manage")) return;
		ConfigManager newConfig = plugin.getConfigManager();
		newConfig.reloadConfig();
		newConfig.reloadMessages();
		plugin.setConfigManager(newConfig);
		BlockFilter.reload();
		plugin.chairManager.reload(plugin);
		plugin.messageManager.reload(plugin);
		player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Rifle's Chairs" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Config Reloaded");
	}

	@Override
	public String name() {
		return plugin.commandManager.reload;
	}

	@Override
	public String info() {
		return "Reloads the config and messages";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}

}
