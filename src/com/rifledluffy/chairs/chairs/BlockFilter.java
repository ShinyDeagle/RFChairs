package com.rifledluffy.chairs.chairs;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class BlockFilter {
    
    private static Set<String> whitelist, blacklist;
    private static Set<Material> carpetTypeSet, slabTypeSet, stairsTypeSet;
    
    static {
        carpetTypeSet = EnumSet.noneOf(Material.class);
        slabTypeSet = EnumSet.noneOf(Material.class);
        stairsTypeSet = EnumSet.noneOf(Material.class);
    
        Arrays.stream(Material.values())
                .filter(m -> !m.name().startsWith("LEGACY_"))
                .forEach(m -> {
                    String name = m.name();
                    if (name.endsWith("_CARPET"))
                        carpetTypeSet.add(m);
                    else if (name.endsWith("_SLAB"))
                        slabTypeSet.add(m);
                    else if (name.endsWith("_STAIRS"))
                        stairsTypeSet.add(m);
                });
    }
    
    public static void reload() {
        FileConfiguration config = RFChairs.getInstance().getConfig();
        whitelist = new HashSet<>(config.getStringList("allowed-chairs"));
        blacklist = new HashSet<>(config.getStringList("blacklisted-chairs"));
    }
    
    public static boolean isWhitelisted(Material type, String category) {
        if (whitelist.contains("ALL_" + category)) return true;
        return whitelist.contains(type.name());
    }
    
    public static boolean isBlacklisted(Material type, String category) {
        if (blacklist.contains("ALL_" + category)) return true;
        return blacklist.contains(type.name());
    }
    
    // validate
    
    public static boolean validateCarpet(Material type) {
        return isWhitelisted(type, "CARPETS") && !isBlacklisted(type, "CARPET");
    }
    
    public static boolean validateSlab(Material type) {
        return isWhitelisted(type, "SLABS") && !isBlacklisted(type, "SLAB");
    }
    
    public static boolean validateStairs(Material type) {
        return isWhitelisted(type, "STAIRS") && !isBlacklisted(type, "STAIRS");
    }
    
    // check
    
    public static boolean isCarpetBlock(Material type) {
        return carpetTypeSet.contains(type);
    }
    
    public static boolean isSlabBlock(Material type) {
        return slabTypeSet.contains(type);
    }
    
    public static boolean isStairsBlock(Material type) {
        return stairsTypeSet.contains(type);
    }
}
