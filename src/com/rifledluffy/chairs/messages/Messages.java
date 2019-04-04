package com.rifledluffy.chairs.messages;

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
	private final static String plotSquared = "denied-by-plotsquared";
	
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
	private final static String defaultPlotSquared = "&8[&6Rifle's Chairs&8] &cYou may not sit in this plot!";

	public static String getDefault(MessageType type) {
		switch (type) {
			default:
				return null;
			case OCCUPIED:
				return Messages.defaultOccupied;
			case TOOFAR:
				return Messages.defaultTooFar;
			case NOPERMS:
				return Messages.defaultNoPerms;
			case TOOMANYITEMS:
				return Messages.defaultTooMany;
			case TOSSED:
				return Messages.defaultTossed;
			case TOSSEDSPEED:
				return Messages.defaultTossedSpeed;
			case TOSSING:
				return Messages.defaultTossing;
			case TOSSINGSPEED:
				return Messages.defaultTossingSpeed;
			case PRIORITYTOSS:
				return Messages.defaultPriority;
			case NOSIGNS:
				return Messages.defaultNoSigns;
			case WORLDGUARD:
				return Messages.defaultWorldGuard;
			case PLOTSQUARED:
				return Messages.defaultPlotSquared;
		}
	}
	
	public static String getMessage(MessageType type) {
		switch (type) {
			default:
				return null;
			case OCCUPIED:
				return Messages.occupied;
			case TOOFAR:
				return Messages.tooFar;
			case NOPERMS:
				return Messages.noPerms;
			case TOOMANYITEMS:
				return Messages.tooMany;
			case TOSSED:
				return Messages.tossed;
			case TOSSEDSPEED:
				return Messages.tossedSpeed;
			case TOSSING:
				return Messages.tossing;
			case TOSSINGSPEED:
				return Messages.tossingSpeed;
			case PRIORITYTOSS:
				return Messages.priority;
			case NOSIGNS:
				return Messages.noSigns;
			case WORLDGUARD:
				return Messages.worldGuard;
			case PLOTSQUARED:
				return Messages.plotSquared;
		}
	}
}
