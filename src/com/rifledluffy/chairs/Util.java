package com.rifledluffy.chairs;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Stairs;

public class Util {
	
	private static RFChairs plugin = RFChairs.getPlugin(RFChairs.class);
	private static ConfigManager configManager = plugin.getConfigManager();

	public static ConfigManager getConfigManager() {
		return configManager;
	}
	
	public static void debug(String message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) player.sendMessage(message);
	}
	
	public static void debug(int message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) player.sendMessage(Integer.toString(message));
	}
	
	public static void debug(Double message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) player.sendMessage(Double.toString(message));
	}
	
	public static void debug(Boolean message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) player.sendMessage(Boolean.toString(message));
	}
	
	public static boolean isStairBlock(Material material) {
		if (StairBlock.from(material) == "null") return false;
		return true;
	}
	
	public static boolean isSlabBlock(Material material) {
		if (SlabBlock.from(material) == "null") return false;
		return true;
	}
	
	public static boolean isCarpetBlock(Material material) {
		if (CarpetBlock.from(material) == "null") return false;
		return true;
	}
	
	public static boolean validateStair(Block block) {
		List<String> whitelist = plugin.getConfig().getStringList("allowed-chairs");
		boolean found = false;
		for (String white : whitelist) if (white.equalsIgnoreCase(StairBlock.from(block.getType()))) found = true;
		if (!found) return false;
		Stairs stair = (Stairs) block.getState().getData();
		if (stair.isInverted()) return false;
		return true;
	}
	
	public static boolean validateSlab(Block block) {
		List<String> whitelist = plugin.getConfig().getStringList("allowed-chairs");
		boolean found = false;
		for (String white : whitelist) if (white.equalsIgnoreCase(SlabBlock.from(block.getType()))) found = true;
		if (!found) return false;
		return true;
	}

	public static boolean validateCarpet(Block block) {
		List<String> whitelist = plugin.getConfig().getStringList("allowed-chairs");
		boolean found = false;
		for (String white : whitelist) if (white.equalsIgnoreCase(CarpetBlock.from(block.getType()))) found = true;
		if (!found) return false;
		return true;
	}
}
