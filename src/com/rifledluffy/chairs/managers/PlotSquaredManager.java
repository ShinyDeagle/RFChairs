package com.rifledluffy.chairs.managers;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag;
import org.bukkit.Location;

public class PlotSquaredManager {

    private PlotAPI api;
    private BooleanFlag seating = new BooleanFlag("seating");

    public PlotSquaredManager() {}

    public void setup() {
        api = new PlotAPI();
        api.addFlag(seating);
    }

    public boolean canSit(Location location) {
        com.github.intellectualsites.plotsquared.plot.object.Location loc = new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        return api.getAllPlots().stream()
                .filter(plot -> plot.getArea().contains(loc))
                .noneMatch(plot -> plot.getFlag(seating).orElse(true));
    }

}
