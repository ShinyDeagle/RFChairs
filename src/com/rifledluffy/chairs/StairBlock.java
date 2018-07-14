/**
 * Borrowed code from BlackScarx's BetterChairs plugin.
 * Just in case it seems familiar.
 * https://github.com/BlackScarx/BetterChairs/blob/master/src/net/blackscarx/betterchairs/StairsBlock.java
 */

package com.rifledluffy.chairs;

import org.bukkit.Material;


public enum StairBlock {

    ACACIA("acacia_stairs"),
    BIRCH_WOOD("birch_wood_stairs"),
    BRICK("brick_stairs"),
    COBBLESTONE("cobblestone_stairs"),
    DARK_OAK("dark_oak_stairs"),
    DARK_PRISMARINE("dark_prismarine_stairs"),
    JUNGLE_WOOD("jungle_wood_stairs"),
    NETHER_BRICK("nether_brick_stairs"),
    OAK_STAIRS("oak_stairs"),
    PRISMARINE_BRICK("prismarine_brick_stairs"),
    PRISMARINE("prismarine_stairs"),
    PURPUR_STAIRS("purpur_stairs"),
    QUARTZ("quartz_stairs"),
    RED_SANDSTONE("red_sandstone_stairs"),
    SANDSTONE("sandstone_stairs"),
    SPRUCE("spruce_stairs"),
    STONE_BRICK("stone_brick_stairs"),
    SPRUCE_WOOD("spruce_wood_stairs"),
    WOOD_STAIRS("wood_stairs");

    final private String name;
    
    StairBlock(String name) {
        this.name = name;
    }

    public static String from(Material stair) {
        for (StairBlock stairs : values()) {
            if (stairs.getName().equalsIgnoreCase(stair.name())) {
                return stairs.getName();
            }
        }
        return "null";
    }

    public String getName() {
        return name;
    }
    
    public static StairBlock[] getList() {
    	return values();
    }

}