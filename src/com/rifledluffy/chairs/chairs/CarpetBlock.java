package com.rifledluffy.chairs.chairs;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CarpetBlock extends BlockFilter {

    public final void init() {
        TYPE_SUFFIX = "_CARPET";
        validMats = Arrays.stream(Material.values())
                .filter(mat -> mat.name().contains(TYPE_SUFFIX))
                .filter(mat -> !mat.name().contains("LEGACY_"))
                .collect(Collectors.toList());
    }

}