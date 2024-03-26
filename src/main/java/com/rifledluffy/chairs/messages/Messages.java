package com.rifledluffy.chairs.messages;

import org.jetbrains.annotations.NotNull;

public class Messages {
    private final static String prefix = "prefix";
    private final static String occupied = "occupied-seat";
    private final static String tooFar = "too-far-from-seat";
    private final static String noPerms = "no-permission";
    private final static String tooMany = "too-many-items";
    private final static String tossed = "tossed-out-of-chair";
    private final static String tossedSpeed = "tossed-out-with-speed";
    private final static String tossing = "tossing-someone";
    private final static String tossingSpeed = "tossing-someone-with-speed";
    private final static String priority = "priority-replaced";
    private final static String noSigns = "no-sign-at-ends";
    private final static String worldGuard = "denied-by-worldguard";

    public static @NotNull String getMessage(@NotNull MessageType type) {
        return switch (type) {
            case OCCUPIED -> Messages.prefix + Messages.occupied;
            case TOOFAR -> Messages.prefix + Messages.tooFar;
            case NOPERMS -> Messages.prefix + Messages.noPerms;
            case TOOMANYITEMS -> Messages.prefix + Messages.tooMany;
            case TOSSED -> Messages.prefix + Messages.tossed;
            case TOSSEDSPEED -> Messages.prefix + Messages.tossedSpeed;
            case TOSSING -> Messages.prefix + Messages.tossing;
            case TOSSINGSPEED -> Messages.prefix + Messages.tossingSpeed;
            case PRIORITYTOSS -> Messages.prefix + Messages.priority;
            case NOSIGNS -> Messages.prefix + Messages.noSigns;
            case WORLDGUARD -> Messages.prefix + Messages.worldGuard;
        };
    }
}
