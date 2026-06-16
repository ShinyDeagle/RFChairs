package com.rifledluffy.chairs.command.commands;

import com.rifledluffy.chairs.MessageManager;
import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.command.CommandManager;
import com.rifledluffy.chairs.messages.MessagePath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HelpCommand implements SubCommand {

    public void onCommand(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        TextComponent.Builder builder = Component.text();
        MessageManager messageManager = RFChairs.getInstance().getMessageManager();
        String rawCommand = "<gold>/" + CommandManager.getMainCommand() + "<cmd></gold><dark_gray> | </dark_gray>";

        builder.append(messageManager.getLang(MessagePath.COMMAND_HELP_HEADER)).appendNewline();
        builder.append(messageManager.getLang(MessagePath.COMMAND_HELP_MAIN)).appendNewline();


        for (SubCommand subCommand : RFChairs.getInstance().getCommandManager().getSubCommands()) {
            if (subCommand.checkPermission(sender)) {
                builder.append(MiniMessage.miniMessage().deserialize(rawCommand, Placeholder.unparsed("cmd", subCommand.name()))).
                        append(subCommand.info().color(NamedTextColor.GRAY)).appendNewline();
            }
        }
        builder.append(messageManager.getLang(MessagePath.COMMAND_HELP_FOOTER));

        messageManager.sendMessageWithoutPrefix(sender, builder);
    }

    @Override
    public void onPlayerCommand(@NotNull Player player, @NotNull List<@NotNull String> args) {
        onCommand(player, args);
    }

    @Override
    public @NotNull String name() {
        return "help";
    }

    @Override
    public @NotNull Component info() {
        return RFChairs.getInstance().getMessageManager().getLang(MessagePath.COMMAND_HELP_INFO);
    }

    @Override
    public @NotNull String @NotNull [] aliases() {
        return new String[0];
    }

    @Override
    public boolean needsPlayer() {
        return false;
    }

    @Override
    public boolean checkPermission(@NotNull Permissible permissible) {
        return true;
    }

    @Override
    public @NotNull List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull List<@NotNull String> args) {
        return List.of();
    }
}