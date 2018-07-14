package com.rifledluffy.chairs.chairs;

import org.bukkit.Material;

public enum CarpetBlock {

    BLACK("black_carpet"),
    BLUE("blue_carpet"),
    BROWN("brown_carpet"),
    CYAN("cyan_carpet"),
    GRAY("gray_carpet"),
    GREEN("green_carpet"),
    LIGHT_BLUE("light_blue_carpet"),
    LIGHT_GRAY("light_gray_carpet"),
    LIME("lime_carpet"),
    MAGENTA("magenta_carpet"),
    ORANGE("orange_carpet"),
    PINK("pink_carpet"),
    PURPLE("purple_carpet"),
    RED("red_carpet"),
	WHITE("white_carpet"),
	YELLOW("yellow_carpet");

    final private String name;

    CarpetBlock(String name) {
        this.name = name;
    }

    public static String from(Material mat) {
        for (CarpetBlock carpet : values()) {
            if (carpet.getName().equalsIgnoreCase(mat.name())) {
                return carpet.getName();
            }
        }
        return "null";
    }

    public String getName() {
        return name;
    }

    public static CarpetBlock[] getList() {
    	return values();
    }
}