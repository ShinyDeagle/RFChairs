package com.rifledluffy.chairs.chairs;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockFilter {

    static List<Material> validMats = new ArrayList<>();
    static String TYPE_SUFFIX;

    public static boolean isBlock(Material mat) {
        return validMats.contains(mat);
    }

    private static boolean inWhiteList(Material mat) {
        List<String> whitelist = RFChairs.getInstance().getConfig().getStringList("allowed-chairs");
        if (whitelist.contains("ALL_" + TYPE_SUFFIX)) return true;
        return whitelist.contains(mat.name());
    }

    private static boolean inBlackList(Material mat) {
        List<String> blacklist = RFChairs.getInstance().getConfig().getStringList("blacklisted-chairs");

        if (blacklist.contains("ALL_" + TYPE_SUFFIX)) return false;
        return blacklist.contains(mat.name());
    }

    public static boolean validate(Material mat) {
        return inWhiteList(mat) && !inBlackList(mat);
    }
}
