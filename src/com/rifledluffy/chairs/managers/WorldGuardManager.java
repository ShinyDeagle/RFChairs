package com.rifledluffy.chairs.managers;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.Chair;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class WorldGuardManager {

	private RFChairs plugin = RFChairs.getInstance();
	public WorldGuard worldGuard;
	public WorldGuardPlugin worldGuardPlugin;
	public RegionContainer container;
	@SuppressWarnings("rawtypes")
	public static Flag flag;

	public WorldGuardManager() {}
	
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
		
		if (!(plugin instanceof WorldGuardPlugin)) return null;
		return (WorldGuardPlugin) plugin;
	}
	
	public boolean validateSeating(Chair chair, Player player) {
		double xPos = chair.getLocation().getX();
		double yPos = chair.getLocation().getY();
		double zPos = chair.getLocation().getZ();

		WorldGuardManager worldManager = plugin.getWorldGuardManager();

		LocalPlayer localPlayer = worldManager.getWorldGuard().wrapPlayer(player);

        BlockVector3 vector3 = BlockVector3.at(xPos, yPos, zPos);

        List<ApplicableRegionSet> regionSetList = getContainer().getLoaded().stream()
                .map(manager -> manager.getApplicableRegions(vector3))
                .collect(Collectors.toList());

        if (regionSetList.size() == 0) return true;

        return regionSetList.stream()
                .allMatch(set -> set.testState(localPlayer, (StateFlag) worldManager.getFlag()));
	}

	public RegionContainer getContainer() {
		return worldGuard.getPlatform().getRegionContainer();
	}
	
	public Flag<?> getFlag() {
		return flag;
	}

}
