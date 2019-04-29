/**
 * Borrowed code from BlackScarx's BetterChairs plugin.
 * Just in case it seems familiar.
 * https://github.com/BlackScarx/BetterChairs/blob/master/src/net/blackscarx/betterchairs/SlabBlock.java
 */

package com.rifledluffy.chairs.chairs;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SlabBlock extends BlockFilter {

    public final void init() {
        TYPE_SUFFIX = "_SLAB";
        validMats = Arrays.stream(Material.values())
                .filter(mat -> mat.name().contains(TYPE_SUFFIX))
                .filter(mat -> !mat.name().contains("LEGACY_"))
                .collect(Collectors.toList());
    }

}