package com.rifledluffy.chairs.messages;

import org.jetbrains.annotations.NotNull;

public class Messages {
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

    private final static String defaultOccupied = "&8[&6Rifle's Chairs&8] &cCan't sit there &7%user%&a, my homie &7%seated%&c sits there.";
    private final static String defaultTooFar = "&8[&6Rifle's Chairs&8] &cCan't sit there &7%user%&c. You are too far!";
    private final static String defaultNoPerms = "&8[&6Rifle's Chairs&8] &cCan't sit there &7%user%&c. I won't allow you!";
    private final static String defaultTooMany = "&8[&6Rifle's Chairs&8] &c&7%user%&c! You have quite a hefty stack of items on your hand. Remove them!";
    private final static String defaultTossed = "&8[&6Rifle's Chairs&8] &cTossed!... by &7%user%.";
    private final static String defaultTossedSpeed = "&8[&6Rifle's Chairs&8] &cYou got chucked off your seat by &7%user%.";
    private final static String defaultTossing = "&8[&6Rifle's Chairs&8] &cYou tossed &7%seated%&c off their seat!";
    private final static String defaultTossingSpeed = "&8[&6Rifle's Chairs&8] &cYou forcibly tossed &7%seated%&c off their seat!";
    private final static String defaultPriority = "&8[&6Rifle's Chairs&8] &cYou forcibly tossed to make space for &7%user%&c, &7%seated%";
    private final static String defaultNoSigns = "&8[&6Rifle's Chairs&8] &cThis seat &8[&7which is a stair&8]&c doesn't have signs on both its ends!";
    private final static String defaultWorldGuard = "&8[&6Rifle's Chairs&8] &cYou may not sit in this region!";

    public static @NotNull String getDefault(@NotNull MessageType type) {
        return switch (type) {
            case OCCUPIED -> Messages.defaultOccupied;
            case TOOFAR -> Messages.defaultTooFar;
            case NOPERMS -> Messages.defaultNoPerms;
            case TOOMANYITEMS -> Messages.defaultTooMany;
            case TOSSED -> Messages.defaultTossed;
            case TOSSEDSPEED -> Messages.defaultTossedSpeed;
            case TOSSING -> Messages.defaultTossing;
            case TOSSINGSPEED -> Messages.defaultTossingSpeed;
            case PRIORITYTOSS -> Messages.defaultPriority;
            case NOSIGNS -> Messages.defaultNoSigns;
            case WORLDGUARD -> Messages.defaultWorldGuard;
        };
    }

    public static @NotNull String getMessage(@NotNull MessageType type) {
        return switch (type) {
            case OCCUPIED -> Messages.occupied;
            case TOOFAR -> Messages.tooFar;
            case NOPERMS -> Messages.noPerms;
            case TOOMANYITEMS -> Messages.tooMany;
            case TOSSED -> Messages.tossed;
            case TOSSEDSPEED -> Messages.tossedSpeed;
            case TOSSING -> Messages.tossing;
            case TOSSINGSPEED -> Messages.tossingSpeed;
            case PRIORITYTOSS -> Messages.priority;
            case NOSIGNS -> Messages.noSigns;
            case WORLDGUARD -> Messages.worldGuard;
        };
    }
}
