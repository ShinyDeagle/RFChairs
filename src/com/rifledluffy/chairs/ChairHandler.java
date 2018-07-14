package com.rifledluffy.chairs;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Stairs;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ChairHandler implements Listener {

	private RFChairs plugin = RFChairs.getPlugin(RFChairs.class);
	private ConfigManager configManager = plugin.getConfigManager();

	public Map<Player, Chair> chairMap = new HashMap<Player, Chair>();
	public List<Chair> chairs = new ArrayList<Chair>();
	public List<String> fakeSeats = new ArrayList<String>();
	public List<Player> leaving = new ArrayList<Player>();
	public Map<Player, TargetInfo> tossed = new HashMap<Player, TargetInfo>();

	/**
	* Listener
	*/

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getPlayer().isSneaking()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getMaterial() != Material.AIR) return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		//I know, I know. A truly disgusting way to handle this.
		if (!(block.getType() == Material.ACACIA_STAIRS
		|| block.getType() == Material.BIRCH_WOOD_STAIRS
		|| block.getType() == Material.BRICK_STAIRS
		|| block.getType() == Material.COBBLESTONE_STAIRS
		|| block.getType() == Material.DARK_OAK_STAIRS
		|| block.getType() == Material.JUNGLE_WOOD_STAIRS
		|| block.getType() == Material.NETHER_BRICK_STAIRS
		|| block.getType() == Material.PURPUR_STAIRS
		|| block.getType() == Material.QUARTZ_STAIRS
		|| block.getType() == Material.RED_SANDSTONE_STAIRS
		|| block.getType() == Material.SANDSTONE_STAIRS
		|| block.getType() == Material.SMOOTH_STAIRS
		|| block.getType() == Material.SPRUCE_WOOD_STAIRS
		|| block.getType() == Material.WOOD_STAIRS)) {
			return;
		};

		Stairs stair = ((Stairs)block.getState().getData());
		if (stair.isInverted()) return;

		if (block.getRelative(BlockFace.UP).getType() != Material.AIR) return;

		if (playerIsSeated(player)) clearPlayer(player);

		if (!blockIsChair(block)) {
			Chair chair = new Chair(player, block);
			boolean done = sitPlayer(chair, player);
			if (!done) return;
			chairs.add(chair);
			if (chairMap.containsKey(player)) return;
			chairMap.put(player, chair);
		}
	}

	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		Player player = (Player) event.getEntity();
		Chair chair = chairMap.get(player);
		if (chair == null || event.getDismounted().getType() != EntityType.ARMOR_STAND) return;
		if (leaving.contains(player)) return;
		leaving.add(player);
		if (tossed.containsKey(player)) {
			TargetInfo info = tossed.get(player);
			LivingEntity attacker = info.getEntity();
			ejectPlayer(chair, player, attacker);
			tossed.remove(player);
			return;
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		boolean exitWhereFacing = plugin.getConfig().getBoolean("exit-seat-where-facing");
		if (leaving.contains(player) && exitWhereFacing) {
			leaving.remove(player);
			Chair chair = chairMap.get(player);
			Location playerLoc = player.getLocation();
			Block chairBlock = chair.getBlock();
			clearPlayer(player);

			Block exit = findExitPoint(playerLoc, chairBlock);

			Location exitLoc = exit.getLocation().add(0.5,0.5,0.5);

			exitLoc.setPitch(playerLoc.getPitch());
			exitLoc.setYaw(playerLoc.getYaw());
			player.teleport(exitLoc);
			return;
		}
	}

	/**
	* Event when seated are damaged by entity
	*/

	@EventHandler
	public void takeDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		LivingEntity attacker = (LivingEntity) event.getDamager();
		Chair chair = chairMap.get(player);
		if (chair == null) return;

		boolean canLaunch = plugin.getConfig().getBoolean("toss-player");
		double minDamage = plugin.getConfig().getDouble("minimum-toss-damage");

		if (event.getDamage() >= minDamage && canLaunch) {
			TargetInfo info = new TargetInfo(attacker, event.getDamage());
			tossed.put(player, info);
			chair.getFakeSeat().remove();
		}
	}

	/**
	* Cancel event if chair was broken
	*/

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		for (Chair chair: chairs) {
			if (samePosition(event.getBlock(), chair.getBlock())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	/**
	* Remove the chair if player disconnected
	*/

	@EventHandler(priority = EventPriority.LOWEST)
	public void quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Chair chair = chairMap.get(player);
		if (chair != null) clearPlayer(player);
	}

	/**
	* Cancel piston events with chairs
	*/

	@EventHandler
	public void pistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			for (Chair chair: chairs) {
				if (samePosition(block, chair.getBlock())) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void pistonRetract(BlockPistonRetractEvent event) {
		for (Block block : event.getBlocks()) {
			for (Chair chair: chairs) {
				if (samePosition(block, chair.getBlock())) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Chair chair = chairMap.get(player);
		if (chair != null) clearPlayer(player);
	}

	/*
	* Sitting and Ejection Methods
	*/

	boolean sitPlayer(Chair chair, Player player) {
		if (player == null) return false;
		if (chair == null || chair.isOccupied()) return false;

		ArmorStand fakeSeat = chair.getFakeSeat();
		if (fakeSeat == null) fakeSeat = generateFakeSeat(chair);

		fakeSeat.addPassenger(player);
		chair.setFakeSeat(fakeSeat);
		fakeSeats.add(fakeSeat.getUniqueId().toString());
		configManager.getData().set("UUIDs", fakeSeats);
		return true;
	}

	void ejectPlayer(Chair chair, Player player, LivingEntity entity) {
		Block exit = findExitPoint(entity.getLocation(), chair.getBlock());

		Location exitLoc = exit.getLocation().add(0.5,0.5,0.5);

		boolean faceAttacker = plugin.getConfig().getBoolean("face-attacker-when-ejected");
		boolean canToss = plugin.getConfig().getBoolean("toss-player");
		double tossVelocity = plugin.getConfig().getDouble("toss-velocity");
		boolean scaleVelocity = plugin.getConfig().getBoolean("scale-toss-velocity");
		double scaleAmount = plugin.getConfig().getDouble("velocity-scale-factor");

		if (!faceAttacker) {
			exitLoc.setPitch(player.getLocation().getPitch());
			exitLoc.setYaw(player.getLocation().getYaw());
		} else {
			exitLoc.setDirection(getVectorDir(player.getLocation(), entity.getLocation()));
		}

		if (canToss && tossVelocity != 0) {
			double finalVelocity = tossVelocity;
			if (scaleVelocity && entity instanceof Player) {
				if (((Player)entity).isSprinting()) finalVelocity = tossVelocity * scaleAmount;
			}
			player.setVelocity(entity.getLocation().getDirection().multiply(finalVelocity));
		}

		player.teleport(exitLoc);
		clearPlayer(player);
	}

	Block findExitPoint(Location entity, Block block) {
		Block blockToCheck = getBlockFromDirection(block, getCardinalDirection(entity));
		boolean foundValidExit = canFitPlayer(blockToCheck) && safePlace(blockToCheck);

		String directions[] = {"north", "east", "south", "west"};
		for (String direction: directions) {
			if (foundValidExit) break;
			blockToCheck = getBlockFromDirection(block, direction);
			foundValidExit = canFitPlayer(blockToCheck) && safePlace(blockToCheck);
		}
		if (!foundValidExit) blockToCheck = block.getRelative(BlockFace.UP);
		return blockToCheck;
	}

	/*
	* Cleaning and Clearing
	*/

	void shutdown(JavaPlugin plugin) {
		clearChairs();
		clearFakeSeats(plugin);
	}

	void clearChair(Chair chair) {
		chair.clear();
	}

	void clearChairs() {
		for (Chair chair: chairs) clearChair(chair);
	}

	void clearPlayer(Player player) {
		Chair chair = chairMap.get(player);
		if (chair == null) return;
		chairMap.remove(player);
		chairs.remove(chair);
		chair.clear();
	}

	public void clearFakeSeats(JavaPlugin plugin) {
		for (String fakeSeat: fakeSeats) {
			Entity armorStand = plugin.getServer().getEntity(UUID.fromString(fakeSeat));
			if (armorStand == null) continue;
			armorStand.remove();
		}
	}

	public void clearFakeSeatsFromFile(JavaPlugin plugin) {
		List<String> fakes = configManager.getData().getStringList("UUIDs");
		int leftoverFakes = fakes.size();
		if (leftoverFakes >= 1) {
			plugin.getServer().getLogger().info("[Rifle Chairs] Detected " + fakes.size() + " leftover seats! Removing...");
			for (String fake: fakes) {
				UUID id = UUID.fromString(fake);
				Entity armorStand = plugin.getServer().getEntity(id);
				if (armorStand == null) continue;
				armorStand.remove();
			}
			configManager.getData().set("UUIDs", fakeSeats);
		} else {
			plugin.getServer().getLogger().info("[Rifle Chairs] No fake seats remaining! Proceeding");
		}
	}

	/**
	* Utilities
	*/

	boolean playerIsSeated(Player player) {
		Chair chair = chairMap.get(player);
		return chair != null;
	}

	boolean sameSeat(Player player, Block block) {
		Chair chair = chairMap.get(player);
		if (chair == null) return false;
		return chair.getBlock() == block;
	}

	boolean samePosition(Block block, Block target) {
		return block.getX() == target.getX() && block.getY() == target.getY() && block.getZ() == target.getZ();
	}

	boolean blockIsChair(Block block) {
		for (Chair chair: chairs) {
			if (chair.getLocation() == null) continue;
			if (samePosition(block, chair.getBlock())) return true;
		}
		return false;
	}

	ArmorStand generateFakeSeat(Chair chair) {
		if (chair == null) return null;
		Location seat = chair.getLocation();
		BlockFace facing = chair.getFacing();
		Vector vector = getVectorFromFace(chair.getBlock(), facing);

		ArmorStand armorStand = (ArmorStand)seat.getWorld().spawnEntity(seat.add(0.5, 0.25, 0.5).setDirection(vector), EntityType.ARMOR_STAND);

		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setInvulnerable(true);
		armorStand.setMarker(true);
		armorStand.setCollidable(false);

		return armorStand;
	}

	String getCardinalDirection(Location loc) {
		float y = loc.getYaw();
		if (y < 0) y += 360;
		y %= 360;
		if (y <= 45 || y >= 315) return "south";
		if (y >= 45 && y <= 135) return "west";
		if (y >= 135 && y <= 225) return "north";
		return "east";
	}

	Block getBlockFromDirection(Block block, String direction) {
		if (direction.equalsIgnoreCase("north")) return block.getRelative(BlockFace.NORTH);
		else if (direction.equalsIgnoreCase("west")) return block.getRelative(BlockFace.WEST);
		else if (direction.equalsIgnoreCase("east")) return block.getRelative(BlockFace.EAST);
		else if (direction.equalsIgnoreCase("south")) return block.getRelative(BlockFace.SOUTH);
		else if (direction.equalsIgnoreCase("down")) return block.getRelative(BlockFace.DOWN);
		else if (direction.equalsIgnoreCase("up")) return block.getRelative(BlockFace.UP);
		return block;
	}

	boolean canFitPlayer(Block block) {
		return block.getType() == Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR;
	}

	boolean safePlace(Block block) {
		return block.getRelative(BlockFace.DOWN).getType() != Material.AIR;
	}

	Vector getVectorDir(Location caster, Location target) {
		return target.clone().subtract(caster.toVector()).toVector();
	}

	Vector getVectorFromFace(Block block, BlockFace face) {
		Location blockLoc = block.getLocation();
		Location faceLoc = block.getRelative(face).getLocation();
		return getVectorDir(blockLoc, faceLoc);
	}
}
