package com.rifledluffy.chairs.command;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.command.commands.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager implements CommandExecutor {
    private final ArrayList<SubCommand> commands = new ArrayList<>();
    private final RFChairs plugin = RFChairs.getInstance();
    //Sub Commands
    public String main = "rfchairs";
    public String help = "help";
    public String info = "info";
    public String reload = "reload";
    public String reset = "reset";
    public String toggle = "toggle";
    public String mute = "mute";

    public CommandManager() {
    }

    public void setup() {
        plugin.getCommand(main).setExecutor(this);

        this.commands.add(new HelpCommand());
        this.commands.add(new InfoCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ResetCommand());
        this.commands.add(new ToggleCommand());
        this.commands.add(new MuteCommand());
    }

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {

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

            ArrayList<String> arrayList = new ArrayList<>();

            arrayList.addAll(Arrays.asList(args));
            arrayList.remove(0);

            try {
                target.onCommand(sender, args);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An error has occurred.");

                e.printStackTrace();
            }
        }

        return true;
    }

    private SubCommand get(String name) {
        for (SubCommand sc : this.commands) {
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
