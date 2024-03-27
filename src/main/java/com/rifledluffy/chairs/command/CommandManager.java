package com.rifledluffy.chairs.command;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.command.commands.*;
import com.rifledluffy.chairs.messages.MessagePath;
import com.rifledluffy.chairs.messages.PlaceHolder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager implements CommandExecutor {
    private static final String MAIN_COMMAND = "rfchairs";
    private final @NotNull ArrayList<@NotNull SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
    }

    public static @NotNull String getMainCommand() {
        return MAIN_COMMAND;
    }

    public void setup() {
        RFChairs.getInstance().getCommand(MAIN_COMMAND).setExecutor(this);

        this.subCommands.add(new HelpCommand());
        //this.commands.add(new InfoCommand());
        this.subCommands.add(new ReloadCommand());
        this.subCommands.add(new ResetCommand());
        this.subCommands.add(new ToggleCommand());
        this.subCommands.add(new MuteCommand());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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
}
