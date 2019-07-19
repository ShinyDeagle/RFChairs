package com.rifledluffy.chairs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.command.commands.HelpCommand;
import com.rifledluffy.chairs.command.commands.InfoCommand;
import com.rifledluffy.chairs.command.commands.MuteCommand;
import com.rifledluffy.chairs.command.commands.ReloadCommand;
import com.rifledluffy.chairs.command.commands.ResetCommand;
import com.rifledluffy.chairs.command.commands.SubCommand;
import com.rifledluffy.chairs.command.commands.ToggleCommand;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	private RFChairs plugin = RFChairs.getInstance();
	
	public CommandManager(){}
	
	//Sub Commands
	public String main = "rfchairs";
	public String help = "help";
    public String info = "info";
    public String reload = "reload";
    public String reset = "reset";
	public String toggle = "toggle";
	public String mute = "mute";

    public void setup() {
        plugin.getCommand(main).setExecutor(this);

        this.commands.add(new HelpCommand());
        this.commands.add(new InfoCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ResetCommand());
        this.commands.add(new ToggleCommand());
        this.commands.add(new MuteCommand());
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase(main)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Missing Arguments. Type /rfchairs help for info");
                return true;
            }

            SubCommand target = this.get(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Invalid subcommand");
                return true;
            }

            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.addAll(Arrays.asList(args));
            arrayList.remove(0);

            try{
                target.onCommand(sender,args);
            }catch (Exception e){
                sender.sendMessage(ChatColor.RED + "An error has occurred.");

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
