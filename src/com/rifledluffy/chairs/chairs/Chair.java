package com.rifledluffy.chairs.chairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.Stairs;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.utility.Util;

public class Chair {
	
	private BukkitRunnable clear;
	private int clearID;
	
	private Location location;
	private Block chair;
	private ArmorStand fakeSeat = null;
	private Player player;
	
	public Chair(Player player, Location location) {
		this.location = location;
		this.player = player;
		this.chair = location.getBlock();
		
		clear = new BukkitRunnable() {

			@Override
			public void run() {
				if (fakeSeat == null) {
					clear();
					return;
				}
				if (fakeSeat.getPassengers().size() == 0) clear();
			}
			
		};
		clearID = clear.runTaskTimer(RFChairs.getInstance(), 20, 20).getTaskId();
	}
	
	/*
	 * Getters
	 */
	
	public ArmorStand getFakeSeat() {
		return fakeSeat;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Block getBlock() {
		return location.getBlock();
	}
	
	public BlockState getBlockState() {
		return chair.getState();
	}
	
	public BlockData getBlockData() {
		return chair.getState().getBlockData();
	}
	
	public BlockFace getFacing() {
		if (!BlockFilter.isStairsBlock(chair.getType())) return null;
		return ((Stairs)chair.getState().getData()).getFacing();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public boolean isOccupied() {
		return !(fakeSeat == null) && !fakeSeat.isEmpty();
	}
	
	/*
	 * Setters
	 */
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/*
	 * Methods
	 */
	
	public void setFakeSeat(ArmorStand armorStand) {
		fakeSeat = armorStand;
	}
	
	public void clear() {
		if (fakeSeat != null) fakeSeat.remove();
		if (player != null) player.removePotionEffect(PotionEffectType.REGENERATION);
		chair = null;
		location = null;
		fakeSeat = null;
		player = null;
		if (Bukkit.getScheduler().isCurrentlyRunning(clearID)) clear.cancel();
	}
	
}
