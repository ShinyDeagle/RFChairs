package com.rifledluffy.chairs.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.updating.Updater;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateCommand extends SubCommand {
	
	RFChairs plugin = RFChairs.getInstance();
	
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
		BukkitRunnable update = new BukkitRunnable() {
			@Override
			public void run() {
				Object[] updates = Updater.getLastUpdate();
				if (updates.length == 2) {
					sender.sendMessage("§6[§eRifle's Chairs§6] New update available:");
					sender.sendMessage("§6New version: §e" + updates[0]);
					sender.sendMessage("§6Your version: §e" + plugin.getDescription().getVersion());
					sender.sendMessage("§6What's new: §e" + updates[1]);
				} else {
					sender.sendMessage("§8[§6Rifle's Chairs§8]: §6Your version: §e" + plugin.getDescription().getVersion());
					sender.sendMessage("§8[§6Rifle's Chairs§8]: §aYou are up to date!");
				}
			}
		};
		update.runTaskAsynchronously(RFChairs.getInstance());
	}
	
	@Override
    public void onCommand(Player player, String[] args) {
		if (!player.hasPermission("rfchairs.update") && !player.hasPermission("rfchairs.manage")) return;
		BukkitRunnable update = new BukkitRunnable() {
			@Override
			public void run() {
				Object[] updates = Updater.getLastUpdate();
				if (updates.length == 2) {
					player.sendMessage("§6[§eRifle's Chairs§6] New update available:");
					player.sendMessage("§6New version: §e" + updates[0]);
					player.sendMessage("§6Your version: §e" + plugin.getDescription().getVersion());
					player.sendMessage("§6What's new: §e" + updates[1]);
				} else {
					player.sendMessage("§8[§6Rifle's Chairs§8]: §6Your version: §e" + plugin.getDescription().getVersion());
					player.sendMessage("§8[§6Rifle's Chairs§8]: §aYou are up to date!");
				}
			}
		};
		update.runTaskAsynchronously(RFChairs.getInstance());
    }

    @Override
    public String name() {
        return plugin.commandManager.update;
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
