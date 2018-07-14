package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;

public class InfoCommand extends SubCommand {
	
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
      sender.sendMessage("§cOnly players can use commands for this plugin.");
	}

    @Override
    public void onCommand(Player player, String[] args) {
    }

    @Override
    public String name() {
        return plugin.commandManager.info;
    }

    @Override
    public String info() {
        return "";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

}