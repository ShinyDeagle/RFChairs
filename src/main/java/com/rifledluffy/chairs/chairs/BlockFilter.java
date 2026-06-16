package com.rifledluffy.chairs.chairs;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockFilter {
    private static Set<@NotNull String> whitelist, blacklist;

    public static void reload() {
        FileConfiguration config = RFChairs.getInstance().getConfig();
        whitelist = new HashSet<>(config.getStringList("whitelisted-chairs"));

        // dataFixerUpper
        if (config.contains("allowed-chairs")) {
            whitelist.addAll(config.getStringList("allowed-chairs"));
            config.set("allowed-chairs", null);
            config.set("whitelisted-chairs", List.copyOf(whitelist));
            RFChairs.getInstance().saveConfig();
        }

        blacklist = new HashSet<>(config.getStringList("blacklisted-chairs"));
    }

    public static boolean isWhitelisted(@NotNull Material type, @NotNull ChairCategory category) {
        return whitelist.contains(category.getConfigName()) || whitelist.contains(type.name());
    }

    public static boolean isBlacklisted(@NotNull Material type, @NotNull ChairCategory category) {
        return blacklist.contains(category.getConfigName()) || blacklist.contains(type.name());
    }

    // validate

    public static boolean validateCarpet(@NotNull Material type) {
        return isWhitelisted(type, ChairCategory.CARPETS) && !isBlacklisted(type, ChairCategory.CARPETS);
    }

    public static boolean validateSlab(@NotNull Material type) {
        return isWhitelisted(type, ChairCategory.SLABS) && !isBlacklisted(type, ChairCategory.SLABS);
    }

    public static boolean validateStairs(@NotNull Material type) {
        return isWhitelisted(type, ChairCategory.STAIRS) && !isBlacklisted(type, ChairCategory.STAIRS);
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

    public enum ChairCategory {
        SLABS("ALL_SLABS"),
        STAIRS("ALL_STAIRS"),
        CARPETS("ALL_CARPETS");

        private final String configName;

        ChairCategory(String configName) {
            this.configName = configName;
        }

        public String getConfigName() {
            return configName;
        }
    }
}
