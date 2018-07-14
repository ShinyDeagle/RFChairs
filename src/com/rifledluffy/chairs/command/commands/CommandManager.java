package com.rifledluffy.chairs.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rifledluffy.chairs.RFChairs;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	private RFChairs plugin = RFChairs.getInstance();

	public CommandManager(){}

		//Sub Commands
		public String main = "rfchairs";
		public String info = "info";
		public String reload = "reload";
		public String reset = "reset";

		public void setup() {
			plugin.getCommand(main).setExecutor(this);

			this.commands.add(new HelpCommand());
			this.commands.add(new InfoCommand());
			this.commands.add(new ReloadCommand());
			this.commands.add(new ResetCommand());
		}

		public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can use commands for this plugin.");
				return true;
			}

			Player player = (Player) sender;

			if (command.getName().equalsIgnoreCase(main)) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Missing Arguments. Type /rfchairs help for info");
					return true;
				}

				SubCommand target = this.get(args[0]);

				if (target == null) {
					player.sendMessage(ChatColor.RED + "Invalid subcommand");
					return true;
				}

				ArrayList<String> arrayList = new ArrayList<String>();

				arrayList.addAll(Arrays.asList(args));
				arrayList.remove(0);

				try{
					target.onCommand(player,args);
				}catch (Exception e){
					player.sendMessage(ChatColor.RED + "An error has occurred.");

					e.printStackTrace();
				}
			}

			return true;
		}

		private SubCommand get(String name) {
			Iterator<SubCommand> subcommands = this.commands.iterator();

			while (subcommands.hasNext()) {
				SubCommand sc = (SubCommand) subcommands.next();

				if (sc.name().equalsIgnoreCase(name)) {
					return sc;
				}

				String[] aliases;
				int length = (aliases = sc.aliases()).length;

				for (int var5 = 0; var5 < length; ++var5) {
					String alias = aliases[var5];
					if (name.equalsIgnoreCase(alias)) {
						return sc;
					}

				}
			}
			return null;
		}

	}
