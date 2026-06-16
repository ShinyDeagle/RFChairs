package com.rifledluffy.chairs.dependencymanagers;

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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldGuardManager {
    private static StateFlag flag;
    private final RFChairs plugin;
    private final @NotNull WorldGuardPlugin worldGuard;

    public WorldGuardManager(final @NotNull RFChairs plugin) {
        this.plugin = plugin;

        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") instanceof WorldGuardPlugin worldGuardPlugin) {
            worldGuard = worldGuardPlugin;
        } else {
            throw new IllegalStateException("WorldGuard plugin is not found!");
        }
    }

    public void register() {
        flag = new StateFlag("allow-seating", true);
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(flag);
            plugin.getComponentLogger().info("Custom Flag Registered!");
        } catch (FlagConflictException | IllegalStateException e) {
            plugin.getComponentLogger().warn("Unable to register custom worldguard flag!", e);
        }
    }

    public boolean validateSeating(@NotNull Chair chair, @NotNull Player player) {
        RegionContainer container = getContainer();
        Location chairLocation = chair.getLocation();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(chairLocation.getWorld()));

        if (regionManager != null) {
            com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(chairLocation);
            LocalPlayer wgPlayer = worldGuard.wrapPlayer(player);
            ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(weLoc.toVector().toBlockPoint());
            return applicableRegions.testState(wgPlayer, getFlag());
        } else {
            return true;
        }
    }

    public RegionContainer getContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public @NotNull StateFlag getFlag() {
        return flag;
    }
}
