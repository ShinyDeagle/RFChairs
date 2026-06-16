package com.rifledluffy.chairs.messages;

import org.jetbrains.annotations.NotNull;

public enum MessagePath {
    COMMAND_HELP_FOOTER("command.help.footer"),
    COMMAND_HELP_HEADER("command.help.header"),
    COMMAND_HELP_MAIN("command.help.main"),
    COMMAND_INVALID_SUBCOMMAND("command.invalid_subcommand"),
    COMMAND_MUTE_DISABLED("command.mute-message.disabled"),
    COMMAND_MUTE_ENABLED("command.mute-message.enabled"),
    COMMAND_NOT_ENOUGH_ARGS("command.not_enough_args"),
    COMMAND_NOT_PLAYER("command.not_player"),
    COMMAND_NO_PERMISSION("command.no-permission"),
    COMMAND_RELOAD_SUCCESS("command.reload.success"),
    COMMAND_RELOAD_INFO("command.reload.info"),
    COMMAND_RESET_SUCCESS("command.reset.success"),
    COMMAND_TOGGLE_DISABLED("command.toggle-disabled"),
    COMMAND_TOGGLE_ENABLED("command.toggle-enabled"),
    NOSIGNS("no-sign-at-ends"),
    // PRIORITYTOSS("priority-replaced"), // todo?
    OCCUPIED("occupied-seat"),
    PLUGIN_PREFIX("pluginPrefix"),
    SEAT_NOPERMS("no-permission"),
    TOOFAR("too-far-from-seat"),
    TOOMANYITEMS("too-many-items"),
    TOSSED("tossed-out-of-chair"),
    TOSSEDSPEED("tossed-out-with-speed"),
    TOSSING("tossing-someone"),
    TOSSINGSPEED("tossing-someone-with-speed"),
    WORLDGUARD("denied-by-worldguard"),
    COMMAND_RESET_INFO("command.reset.info"),
    COMMAND_TOGGLE_INFO("command.toggle.info"),
    COMMAND_MUTE_INFO("command.mute.info"),
    COMMAND_HELP_INFO("command.help.info");

    private final @NotNull String path;

    MessagePath(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getPath() {
        return path;
    }
}
