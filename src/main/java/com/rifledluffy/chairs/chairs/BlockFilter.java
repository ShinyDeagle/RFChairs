package com.rifledluffy.chairs.chairs;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BlockFilter {
    private static Set<@NotNull String> whitelist, blacklist;

    public static void reload() {
        FileConfiguration config = RFChairs.getInstance().getConfig();
        whitelist = new HashSet<>(config.getStringList("allowed-chairs"));
        blacklist = new HashSet<>(config.getStringList("blacklisted-chairs"));
    }

    public static boolean isWhitelisted(@NotNull Material type, @NotNull String category) {
        if (whitelist.contains("ALL_" + category)) return true;
        return whitelist.contains(type.name());
    }

    public static boolean isBlacklisted(@NotNull Material type, @NotNull String category) {
        if (blacklist.contains("ALL_" + category)) return true;
        return blacklist.contains(type.name());
    }

    // validate

    public static boolean validateCarpet(@NotNull Material type) {
        return isWhitelisted(type, "CARPETS") && !isBlacklisted(type, "CARPET");
    }

    public static boolean validateSlab(@NotNull Material type) {
        return isWhitelisted(type, "SLABS") && !isBlacklisted(type, "SLAB");
    }

    public static boolean validateStairs(@NotNull Material type) {
        return isWhitelisted(type, "STAIRS") && !isBlacklisted(type, "STAIRS");
    }

    // check

    public static boolean isCarpetBlock(@NotNull Material type) {
        return Tag.WOOL_CARPETS.isTagged(type);
    }

    public static boolean isSlabBlock(@NotNull Material type) {
        return Tag.SLABS.isTagged(type);
    }

    public static boolean isStairsBlock(@NotNull Material type) {
        return Tag.STAIRS.isTagged(type);
    }
}
