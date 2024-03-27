package com.rifledluffy.chairs.command;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.command.commands.*;
import com.rifledluffy.chairs.messages.MessagePath;
import com.rifledluffy.chairs.messages.PlaceHolder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private static final String MAIN_COMMAND = "rfchairs";
    private final @NotNull ArrayList<@NotNull SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
    }

    public static @NotNull String getMainCommand() {
        return MAIN_COMMAND;
    }

    public void setup() {
        PluginCommand mainCmd = RFChairs.getInstance().getCommand(MAIN_COMMAND);
        if (mainCmd != null) {
            mainCmd.setExecutor(this);
            mainCmd.setTabCompleter(this);
        } else {
            RFChairs.getInstance().getComponentLogger().error("Could not get main command! Commands WILL be broken!");
        }

        this.subCommands.add(new HelpCommand());
        //this.commands.add(new InfoCommand());
        this.subCommands.add(new ReloadCommand());
        this.subCommands.add(new ResetCommand());
        this.subCommands.add(new ToggleCommand());
        this.subCommands.add(new MuteCommand());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            RFChairs.getInstance().getMessageManager().sendLang(sender, MessagePath.COMMAND_NOT_ENOUGH_ARGS);
            return true;
        }

        SubCommand target = this.getSubcommand(args[0]);

        if (target == null) {
            RFChairs.getInstance().getMessageManager().sendLang(sender, MessagePath.COMMAND_INVALID_SUBCOMMAND,
                    Placeholder.unparsed(PlaceHolder.ARGUMENT.getPlaceholder(), args[0]));
            return true;
        }

        // clear args from subcommand
        ArrayList<String> subcommandArgList = new ArrayList<>(Arrays.asList(args));
        subcommandArgList.remove(0);

        if (target.checkPermission(sender)) {
            if (target.needsPlayer()) {
                if (sender instanceof Player player) {
                    target.onPlayerCommand(player, subcommandArgList);
                } else {
                    RFChairs.getInstance().getMessageManager().sendLang(sender, MessagePath.COMMAND_NOT_PLAYER);
                }
            } else {
                target.onCommand(sender, subcommandArgList);
            }
        } else {
            RFChairs.getInstance().getMessageManager().sendLang(sender, MessagePath.COMMAND_NO_PERMISSION);
        }

        return true;
    }

    private @Nullable SubCommand getSubcommand(@NotNull String name) {
        for (SubCommand sc : this.subCommands) {
            if (sc.name().equalsIgnoreCase(name)) {
                return sc;
            }

            String[] aliases = sc.aliases();
            for (String alias : aliases) {
                if (name.equalsIgnoreCase(alias)) {
                    return sc;
                }

            }
        }
        return null;
    }

    public @NotNull ArrayList<@NotNull SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();

            for (SubCommand subCommand : subCommands) {
                if (subCommand.checkPermission(sender)) {
                    result.add(subCommand.name());
                }
            }

            return result;
        } else if (args.length > 1) {
            SubCommand subCommand = this.getSubcommand(args[0]);

            if (subCommand != null && subCommand.checkPermission(sender)) {
                // clear args from subcommand
                ArrayList<String> subcommandArgList = new ArrayList<>(Arrays.asList(args));
                subcommandArgList.remove(0);

                return subCommand.onTabComplete(sender, subcommandArgList);
            }
        }

        return null;
    }
}
