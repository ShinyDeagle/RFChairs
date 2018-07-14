package com.rifledluffy.chairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.Stairs;

public class Chair {

	private Block chair;
	private ArmorStand fakeSeat = null;
	private Player seated;

	Chair(Player player, Block block) {
		chair = block;
		seated = player;
	}

	/*
	* Getters
	*/

	ArmorStand getFakeSeat() {
		return fakeSeat;
	}

	Player getSeated() {
		return seated;
	}

	Block getBlock() {
		return chair;
	}

	
	BlockState getBlockState() {
		return chair.getState();
	}
	
	BlockFace getFacing() {
		return ((Stairs)chair.getState().getData()).getFacing();
	}
	
	Location getLocation() {
		return chair.getLocation();
	}

	boolean isOccupied() {
		return !(fakeSeat == null) && !fakeSeat.isEmpty();
	}

	/*
	* Methods
	*/

	void setFakeSeat(ArmorStand armorStand) {
		fakeSeat = armorStand;
	}

	void clear() {
		if (fakeSeat != null) fakeSeat.remove();
		chair = null;
		fakeSeat = null;
		seated = null;
	}

}
