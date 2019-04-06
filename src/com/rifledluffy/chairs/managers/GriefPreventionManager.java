package com.rifledluffy.chairs.managers;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class GriefPreventionManager {

    private GriefPrevention instance;

    public GriefPreventionManager() {}

    public void setup() {
        instance = GriefPrevention.instance;
    }

    public boolean isAdminClaim(Location location) {
        if (location == null) return false;
        if (instance.dataStore == null) return false;
        Claim claim = instance.dataStore.getClaimAt(location, true, null);
        return claim.isAdminClaim();
    }

}
