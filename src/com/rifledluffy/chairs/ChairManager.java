package com.rifledluffy.chairs;

import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.chairs.Chair;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.events.ChairCheckEvent;
import com.rifledluffy.chairs.events.ChairLeaveEvent;
import com.rifledluffy.chairs.events.ChairReplaceEvent;
import com.rifledluffy.chairs.events.ChairSitEvent;
import com.rifledluffy.chairs.events.ChairTossEvent;
import com.rifledluffy.chairs.messages.MessageConstruct;
import com.rifledluffy.chairs.messages.MessageEvent;
import com.rifledluffy.chairs.messages.MessageType;
import com.rifledluffy.chairs.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChairManager implements Listener {
	
	private RFChairs plugin = RFChairs.getPlugin(RFChairs.class);
	private ConfigManager configManager = plugin.getConfigManager();
	private FileConfiguration config = configManager.getConfig();
	
	private Map<UUID, Chair> chairMap = new HashMap<>();
	public List<Chair> chairs = new ArrayList<>();
	private List<String> fakeSeats = new ArrayList<>();
	private List<UUID> leaving = new ArrayList<>();
	private List<UUID> orienting = new ArrayList<>();
	private Map<UUID, TargetInfo> tossed = new HashMap<>();
	
	public List<UUID> toggled = new ArrayList<>();
	
	private PotionEffect regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 655200, config.getInt("regen-potency", 0), false, false);
	private boolean regenWhenSitting;
	
	//Update related
	private boolean disableCurrentUpdate;
	private boolean disableUpdates;
	
	//Kick them off their seat if they take damage
	private boolean canLaunch;
	
	//Toss them off their seat if they take damage
	private boolean canToss;
	//The minimum damage needed for a player to be tossed
	private double minDamage;
	
	//Toss Configuration
	private boolean faceAttacker;
	private double tossVelocity;
	private boolean scaleVelocity;
	private double scaleAmount;
	
	//Maximum amount of items on hand that they can have when trying to sit
	private int maxItemSit;
	private double maxDistance;
	private boolean checkForSigns;
	private boolean priorityIfHasPerm;
	private boolean trapSeats;
	private boolean requireEmptyHand;
	
	//Mostly buggy, probably going to remove soon.
	private boolean exitWhereFacing;

	public Vector stairSeatingPosition;
	public Vector slabSeatingPosition;
	public Vector carpetSeatingPosition;

	private List<World> disabledWorlds = new ArrayList<>();

	public void reload(RFChairs plugin) {
		configManager = plugin.getConfigManager();
		config = configManager.getConfig();
		
		regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 655200, config.getInt("regen-potency", 0), false, false);
		regenWhenSitting = config.getBoolean("regen-when-sitting", true);
		
		disableCurrentUpdate = config.getBoolean("disable-update-message-if-on-latest", false);
		disableUpdates = config.getBoolean("disable-update-messages", false);
		
		faceAttacker = config.getBoolean("face-attacker-when-ejected", false);
		canToss = config.getBoolean("toss-player", false);
		tossVelocity = config.getDouble("toss-velocity", 0.5D);
		scaleVelocity = config.getBoolean("scale-toss-velocity", true);
		scaleAmount = config.getDouble("velocity-scale-factor", 2D);
		canLaunch = config.getBoolean("eject-player-on-damage", true);
		minDamage = config.getDouble("minimum-eject-damage", 2D);
		exitWhereFacing = config.getBoolean("exit-seat-where-facing", false);
		maxItemSit = config.getInt("max-item-count-to-sit", -1);
		maxDistance = config.getDouble("max-distance", 2D);
		checkForSigns = config.getBoolean("require-signs-at-end", false);
		priorityIfHasPerm = config.getBoolean("priority-on-if-has-perm", true);
		trapSeats = config.getBoolean("allow-trap-door-chairs", true);
		requireEmptyHand = config.getBoolean("require-empty-hand", false);

		try {
			String[] vectorString = Objects.requireNonNull(config.getString("stair-seating-position", "0.5,0.3,0.5")).split(",");
			stairSeatingPosition = new Vector(Double.parseDouble(vectorString[0]), Double.parseDouble(vectorString[1]), Double.parseDouble(vectorString[2]));
		} catch (NumberFormatException e) {
			stairSeatingPosition = new Vector(0.5D,0.3D,0.5);
		}

        try {
            String[] vectorString = Objects.requireNonNull(config.getString("slab-seating-position", "0.5,0.3,0.5")).split(",");
            slabSeatingPosition = new Vector(Double.parseDouble(vectorString[0]), Double.parseDouble(vectorString[1]), Double.parseDouble(vectorString[2]));
        } catch (NumberFormatException e) {
            slabSeatingPosition= new Vector(0.5,0.3D,0.5);
        }

        try {
            String[] vectorString = Objects.requireNonNull(config.getString("carpet-seating-position", "0.5,-0.15,0.5")).split(",");
            carpetSeatingPosition = new Vector(Double.parseDouble(vectorString[0]), Double.parseDouble(vectorString[1]), Double.parseDouble(vectorString[2]));
        } catch (NumberFormatException e) {
            carpetSeatingPosition= new Vector(0.5,-0.15D,0.5);
        }

		disabledWorlds = config.getStringList("disabled-worlds").stream()
				.map(Bukkit::getWorld)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
	
	@EventHandler
	public void onChairSit(ChairSitEvent event) {
		Chair chair = event.getChair();
		if (chair == null) return;
		if (chair.getLocation() == null) return;
		if (event.getPlayer() == null) return;
		
		if (plugin.hasWorldGuard()) if (!plugin.getWorldGuardManager().validateSeating(chair, event.getPlayer())) {
			Util.callEvent(new MessageEvent(MessageType.WORLDGUARD, event.getPlayer()));
			return;
		}
		
		Player player = event.getPlayer();
		
		if (Util.playerIsSeated(player.getUniqueId(), chairMap)) clearPlayer(player);
		boolean done = sitPlayer(chair, player);
		if (!done) return;
		chairs.add(chair);
		chairMap.put(player.getUniqueId(), chair);
		if (regenWhenSitting) player.addPotionEffect(regenEffect);
	}
	
	@EventHandler
	public void onChairLeave(ChairLeaveEvent event) {
		Chair chair = event.getChair();
		if (chair == null) return;
		if (chair.getLocation() == null) return;
		
		Block block = chair.getLocation().getBlock();
		Player player = event.getPlayer();
		boolean flag = event.exitWhereFacing();

		Location exit = block.getLocation();
		
		clearPlayer(player);
		player.removePotionEffect(PotionEffectType.REGENERATION);
		if (flag) exit = findExitPoint(player.getLocation(), block).getLocation();
		if (Util.surroundedBlock(block) || Util.nearLiquid(player.getLocation().getBlock())) exit = block.getRelative(BlockFace.UP).getLocation();
		exit.setDirection(player.getEyeLocation().getDirection());
		exit.add(0.5,0,0.5);
		player.teleport(exit);
		if (!flag) {
			Vector v = player.getEyeLocation().getDirection();
			v.setY(0);
			v.normalize();
			v.setY(1);
			player.setVelocity(v.multiply(0.25));
		}
	}
	
	@EventHandler
	public void onToss(ChairTossEvent event) {
		Chair chair = event.getChair();
		if (chair == null) return;
		if (chair.getLocation() == null) return;
		
		Block block = chair.getBlock();
		Player player = event.getPlayer();
		Entity attacker = event.getAttacker();
		TargetInfo info = event.getInfo();
		boolean silent = event.isSilent();
		
		ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player, false);
		Util.callEvent(leaveEvent);
		
		if (!silent) {
			MessageEvent messageEvent;
			MessageEvent otherEvent = null;
			if (attacker instanceof Player) {
				Player playerEntity = (Player) attacker;
				if (info != null && !info.isSprinting()) messageEvent = new MessageEvent(MessageType.TOSSED, MessageConstruct.DEFENSIVE, player, playerEntity);
				else messageEvent = new MessageEvent(MessageType.TOSSEDSPEED, MessageConstruct.DEFENSIVE, player, playerEntity);
	
				if (info != null && !info.isSprinting()) otherEvent = new MessageEvent(MessageType.TOSSING, MessageConstruct.OFFENSIVE, playerEntity, player);
				else otherEvent = new MessageEvent(MessageType.TOSSINGSPEED, MessageConstruct.OFFENSIVE, playerEntity, player);
			}
			else messageEvent = new MessageEvent(MessageType.TOSSED, player, attacker);
			Util.callEvent(messageEvent);
			if (otherEvent != null) Util.callEvent(otherEvent);
		}
		
		if (canToss) ejectPlayer(block, player, attacker);
	}
	
	@EventHandler
	public void onChairReplace(ChairReplaceEvent event) {
		Chair chair = event.getChair();
		if (chair == null) return;
		if (chair.getLocation() == null) return;
		
		Block block = chair.getBlock();
		Player player = event.getPlayer();
		Player seated = event.getReplaced();
		
		if (player.getUniqueId() == seated.getUniqueId()) return;

		ChairTossEvent tossEvent = new ChairTossEvent(chair, seated, player, null, true);
		Util.callEvent(tossEvent);

		MessageEvent tossed = new MessageEvent(MessageType.TOSSED, MessageConstruct.DEFENSIVE, seated, player);
		Util.callEvent(tossed);
		
		ChairSitEvent sitEvent = new ChairSitEvent(new Chair(player, block.getLocation()), player);
		Util.callEvent(sitEvent);
		
		MessageEvent tosser = new MessageEvent(MessageType.TOSSING, MessageConstruct.OFFENSIVE, player, seated);
		Util.callEvent(tosser);
	}
	
	@EventHandler
	public void onChairCheck(ChairCheckEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		Block block = event.getBlock();

		if (!player.hasPermission("rfchairs.use")) {
			Util.callEvent(new MessageEvent(MessageType.NOPERMS, player));
			return;
		}
		
		if (player.getLocation().distance(block.getLocation().add(0.5, 0, 0.5)) >= maxDistance) {
			Util.callEvent(new MessageEvent(MessageType.TOOFAR, player));
			return;
		}
		
		if (maxItemSit > 0 && item.getAmount() > maxItemSit) {
			Util.callEvent(new MessageEvent(MessageType.TOOMANYITEMS, player));
			return;
		}
		
		if (!Util.blockIsChair(block, chairs)) {
			ChairSitEvent sitEvent = new ChairSitEvent(new Chair(player, block.getLocation()), player);
			Util.callEvent(sitEvent);
		} else {
			Chair chair = Util.getChairFromBlock(block, chairs);
			if (chair == null) return;
			if (chair.getFakeSeat() != null && chair.getFakeSeat().getPassengers().size() == 0) {
				chair.setPlayer(player);
				chair.getFakeSeat().addPassenger(player);
				if (regenWhenSitting) player.addPotionEffect(regenEffect);
				return;
			}
			if (player.hasPermission("rfchairs.priority") && priorityIfHasPerm) {
				ChairReplaceEvent replaceEvent = new ChairReplaceEvent(chair, chair.getPlayer(), player);
				Util.callEvent(replaceEvent);
				return;
			}
			if (chair.getPlayer() == null) return;
			if (player.getUniqueId().equals(chair.getPlayer().getUniqueId())) {
				orienting.add(player.getUniqueId());
				Location loc = chair.getFakeSeat().getLocation();
				loc.setDirection(player.getEyeLocation().getDirection().setY(0));
				chair.getFakeSeat().remove();
				ArmorStand newFakeSeat = Util.generateFakeSeatDir(chair, loc.getDirection());
				chair.setFakeSeat(newFakeSeat);
				chair.getFakeSeat().addPassenger(player);
			} else {
				MessageEvent occupied = new MessageEvent(MessageType.OCCUPIED, MessageConstruct.OFFENSIVE, player, chair.getPlayer());
				Util.callEvent(occupied);
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (disabledWorlds.size() > 0 && disabledWorlds.contains(event.getPlayer().getWorld())) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getPlayer().isSneaking()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (toggled.contains(event.getPlayer().getUniqueId())) return;
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (block == null) return;
		
		if (requireEmptyHand && item != null) return;
		
		if (item != null && item.getType() != Material.AIR) {
			if (item.getType().isBlock()) return;
		}
		
		if (!BlockFilter.isStairsBlock(block.getType())
			&& !BlockFilter.isCarpetBlock(block.getType())
			&& !BlockFilter.isSlabBlock(block.getType())) return;
		
		if (BlockFilter.isStairsBlock(block.getType())) {
			if (!Util.validStair(block)) return;
			else {
				if (checkForSigns) {
					if (!Util.validCouch(block)) {
						if (!trapSeats) return;
						if (!Util.throneChair(block)) {
							Util.callEvent(new MessageEvent(MessageType.NOSIGNS, player));
							return;
						}
					} else if (block.getRelative(BlockFace.UP).getType() != Material.AIR) return;
				}
				if (trapSeats && block.getRelative(BlockFace.UP).getType() != Material.AIR) {
					if (!Util.throneChair(block)) return;
				}
			}
		} else if (block.getRelative(BlockFace.UP).getType() != Material.AIR) return;
		
		if (BlockFilter.isSlabBlock(block.getType())) if (!Util.validSlab(block)) return;
		
		if (BlockFilter.isCarpetBlock(block.getType())) if (!Util.validCarpet(block)) return;
		
		if (event.getBlockFace() == BlockFace.DOWN) return;
		
		if (!player.hasPermission("rfchairs.use")) {
			Util.callEvent(new MessageEvent(MessageType.NOPERMS, player));
			return;
		}
		
		Util.callEvent(new ChairCheckEvent(block, player));
	}
	
	@EventHandler
	public void onChangeGameMode(PlayerGameModeChangeEvent event) {
		if (event.isCancelled()) return;
		GameMode gameMode = event.getNewGameMode();
		Chair chair = chairMap.get(event.getPlayer().getUniqueId());
		if (gameMode == GameMode.SPECTATOR && chair != null) clearChair(chair);
	}
	
	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		Chair chair = chairMap.get(player.getUniqueId());
		event.getDismounted();
		if (chair == null || event.getDismounted().getType() != EntityType.ARMOR_STAND) return;
		if (leaving.contains(player.getUniqueId())) return;
		if (orienting.contains(player.getUniqueId())) {
			orienting.remove(player.getUniqueId());
			return;
		}
		if (tossed.containsKey(player.getUniqueId())) {
			TargetInfo info = tossed.get(player.getUniqueId());
			Entity attacker = info.getEntity();
			if (info.hasSource() && info.livingSource()) attacker = info.getLivingSource();
			
			ChairTossEvent tossEvent = new ChairTossEvent(chairMap.get(player.getUniqueId()), player, attacker, info, false);
			Util.callEvent(tossEvent);
			tossed.remove(player.getUniqueId());
			return;
		}
		leaving.add(player.getUniqueId());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (leaving.contains(player.getUniqueId())) {
			ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chairMap.get(player.getUniqueId()), player, exitWhereFacing);
			Util.callEvent(leaveEvent);
			leaving.remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void takeDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		Entity attacker = event.getDamager();
		ProjectileSource source = null;
		if (attacker instanceof Projectile) source = ((Projectile) attacker).getShooter();
		Chair chair = chairMap.get(player.getUniqueId());
		if (chair == null) return;
		
		if (event.getDamage() >= minDamage && canLaunch) {
			TargetInfo info;
			if (source == null) info = new TargetInfo(attacker, event.getDamage());
			else info = new TargetInfo(source, attacker, event.getDamage());
			tossed.put(player.getUniqueId(), info);
			if (chair.getFakeSeat() != null) chair.getFakeSeat().removePassenger(player);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		chairs.stream()
				.filter(Objects::nonNull)
				.filter(c -> c.getLocation() != null)
				.filter(c -> Util.samePosition(event.getBlock(), c.getBlock()))
				.findAny()
				.ifPresent(c -> {
					if (!event.getPlayer().hasPermission("rfchairs.break")) event.setCancelled(true);
				});
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Chair chair = chairMap.get(player.getUniqueId());
        ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player, exitWhereFacing);
        Util.callEvent(leaveEvent);
    }

    @EventHandler
    public void pistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
        	for (Chair chair: chairs) {
        		if (chair == null) continue;
    			if (chair.getLocation() == null) continue;
        		if (Util.samePosition(block, chair.getBlock())) {
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
    			if (chair == null) continue;
    			if (chair.getLocation() == null) continue;
    			if (Util.samePosition(block, chair.getBlock())) {
        			event.setCancelled(true);
        			break;
        		}
        	}
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
    	Player player = event.getEntity();
        Chair chair = chairMap.get(player.getUniqueId());
        ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player);
        Util.callEvent(leaveEvent);
    }

	/*
	 * Sitting and Ejection Methods
	 */
	
	private boolean sitPlayer(Chair chair, Player player) {
		if (player == null) return false;
		if (chair == null || chair.isOccupied()) return false;
		
		ArmorStand fakeSeat = chair.getFakeSeat();
		if (fakeSeat == null) fakeSeat = Util.generateFakeSeat(chair);

		fakeSeat.addPassenger(player);
		chair.setFakeSeat(fakeSeat);
		fakeSeats.add(fakeSeat.getUniqueId().toString());
		configManager.getData().set("UUIDs", fakeSeats);
		return true;
	}
	
	private void ejectPlayer(Block block, Player player, Entity entity) {
		Block exit = block;
		
		if (exitWhereFacing) exit = findExitPoint(entity.getLocation(), block);
		
		Location exitLoc = exit.getLocation().add(0.5,0.5,0.5);
		
		if (!canToss) return;
		
		if (!faceAttacker) {
			exitLoc.setPitch(player.getLocation().getPitch());
			exitLoc.setYaw(player.getLocation().getYaw());
		} else {
			exitLoc.setDirection(Util.getVectorDir(player.getLocation(), entity.getLocation()));
		}
		
		if (tossVelocity != 0) {
			double finalVelocity = tossVelocity;
			if (scaleVelocity && entity instanceof Player) {
				if (((Player)entity).isSprinting()) finalVelocity = tossVelocity * scaleAmount;
			}
			player.setVelocity(exitLoc.getDirection().multiply(finalVelocity));
		}
	}
	
	private Block findExitPoint(Location entity, Block block) {
		Block blockToCheck = Util.getBlockFromDirection(block, Util.getCardinalDirection(entity));
		boolean foundValidExit = Util.canFitPlayer(blockToCheck) && Util.safePlace(blockToCheck);

		String[] directions = {"north", "east", "south", "west"};
		for (String direction: directions) {
			if (foundValidExit) break;
			blockToCheck = Util.getBlockFromDirection(block, direction);
			foundValidExit = Util.canFitPlayer(blockToCheck) && Util.safePlace(blockToCheck);
		}
		if (!foundValidExit) blockToCheck = block.getRelative(BlockFace.UP);
		return blockToCheck;
	}
	
	/*
	 * Cleaning and Clearing
	 */
	
	void saveToggled() {
		List<String> ids = new ArrayList<>();
		if (toggled == null || toggled.size() == 0) configManager.getData().set("Toggled", new ArrayList<String>());
		for (UUID id : toggled) ids.add(id.toString());
		plugin.getServer().getLogger().info("[RFChairs] Saving " + ids.size() + " Players that had toggled off.");
		configManager.getData().set("Toggled", ids);
	}
	
	void loadToggled() {
		List<String> toggled = configManager.getData().getStringList("Toggled");
		if (toggled.size() == 0) return;
		plugin.getServer().getLogger().info("[RFChairs] " + toggled.size() + " Players had toggled off. Adding Them...");

		toggled.stream()
				.map(UUID::fromString)
				.forEach(id -> this.toggled.add(id));

		configManager.getData().set("Toggled", new ArrayList<String>());
	}
	
	void shutdown() {
		clearChairs();
		clearFakeSeats();
	}
	
	private void clearChair(Chair chair) {
		if (chair == null) return;
		if (chair.getPlayer() != null) chair.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
		chair.clear();
	}
	
	private void clearChairs() {
		for (Chair chair: chairs) clearChair(chair);
	}
	
	private void clearPlayer(Player player) {
		Chair chair = chairMap.get(player.getUniqueId());
		if (chair == null) return;
		chairMap.remove(player.getUniqueId());
		chairs.remove(chair);
		chair.clear();
	}
	
	public void clearFakeSeats() {
		for (Chair chair : chairs) clearChair(chair);
	}
	
	public void clearFakeSeatsFromFile(JavaPlugin plugin) {
		List<String> fakes = configManager.getData().getStringList("UUIDs");
		int leftoverFakes = fakes.size();
		if (leftoverFakes >= 1) {
			plugin.getServer().getLogger().info("[RFChairs] Detected " + fakes.size() + " leftover seats! Removing...");
			for (String fake: fakes) {
				UUID id = UUID.fromString(fake);
				Entity armorStand = plugin.getServer().getEntity(id);
				if (armorStand == null) continue;
				armorStand.remove();
			}
			configManager.getData().set("UUIDs", new ArrayList<UUID>());
		} else {
			plugin.getServer().getLogger().info("[RFChairs] No fake seats remaining! Proceeding");
		}
	}

}
