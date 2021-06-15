package com.rifledluffy.chairs.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.Chair;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardManager {

    private RFChairs plugin = RFChairs.getInstance();
    public WorldGuard worldGuard;
    public WorldGuardPlugin worldGuardPlugin;
    public RegionContainer container;
    public static StateFlag flag;

    public WorldGuardManager() {
    }

    public void setup() {
        worldGuard = WorldGuard.getInstance();
    }

    public void register() {
        flag = new StateFlag("allow-seating", true);
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(flag);
            plugin.log("Custom Flag Registered!");
        } catch (FlagConflictException e) {
            plugin.log("Unable to register custom worldguard flag!");
        }
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public boolean validateSeating(Chair chair, Player player) {
        RegionContainer container = getContainer();
        Location loc = chair.getLocation();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager != null) {
            com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(loc);
            LocalPlayer wgPlayer = getWorldGuard().wrapPlayer(player);
            ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(weLoc.toVector().toBlockPoint());
            return applicableRegions.testState(wgPlayer, getFlag());
        } else {
            return true;
        }
    }

    public RegionContainer getContainer() {
        return worldGuard.getPlatform().getRegionContainer();
    }

    public StateFlag getFlag() {
        return flag;
    }

}
