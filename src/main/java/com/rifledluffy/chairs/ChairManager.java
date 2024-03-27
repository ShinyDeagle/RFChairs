package com.rifledluffy.chairs;

import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.chairs.Chair;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.events.*;
import com.rifledluffy.chairs.messages.MessageEvent;
import com.rifledluffy.chairs.messages.MessagePath;
import com.rifledluffy.chairs.messages.PlaceHolder;
import com.rifledluffy.chairs.utility.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;
import java.util.stream.Collectors;

public class ChairManager implements Listener {
    private final @NotNull RFChairs plugin = RFChairs.getPlugin(RFChairs.class);
    private final @NotNull Map<@NotNull UUID, @NotNull Chair> chairMap = new HashMap<>();
    private final @NotNull List<@NotNull String> fakeSeats = new ArrayList<>();
    private final @NotNull List<@NotNull UUID> leaving = new ArrayList<>();
    private final @NotNull List<@NotNull UUID> orienting = new ArrayList<>();
    private final @NotNull Map<@NotNull UUID, @NotNull TargetInfo> tossed = new HashMap<>();
    private final @NotNull List<@NotNull Chair> chairs = new ArrayList<>();
    private final @NotNull List<@NotNull UUID> toggled = new ArrayList<>();
    private Vector stairSeatingPosition;
    private Vector slabSeatingPosition;
    private Vector carpetSeatingPosition;
    private ConfigManager configManager = plugin.getConfigManager();
    private FileConfiguration config = configManager.getConfig();
    private PotionEffect regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 655200, config.getInt("regen-potency", 0), false, false);
    private boolean regenWhenSitting;
    //Update related
    //private boolean disableCurrentUpdate; //todo
    //private boolean disableUpdates; //todo
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
    private @NotNull List<@NotNull World> disabledWorlds = new ArrayList<>();

    public void reload(RFChairs plugin) {
        configManager = plugin.getConfigManager();
        config = configManager.getConfig();

        regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 655200, config.getInt("regen-potency", 0), false, false);
        regenWhenSitting = config.getBoolean("regen-when-sitting", true);

        //disableCurrentUpdate = config.getBoolean("disable-update-message-if-on-latest", false);
        //disableUpdates = config.getBoolean("disable-update-messages", false);

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
            stairSeatingPosition = new Vector(0.5D, 0.3D, 0.5);
        }

        try {
            String[] vectorString = Objects.requireNonNull(config.getString("slab-seating-position", "0.5,0.3,0.5")).split(",");
            slabSeatingPosition = new Vector(Double.parseDouble(vectorString[0]), Double.parseDouble(vectorString[1]), Double.parseDouble(vectorString[2]));
        } catch (NumberFormatException e) {
            slabSeatingPosition = new Vector(0.5, 0.3D, 0.5);
        }

        try {
            String[] vectorString = Objects.requireNonNull(config.getString("carpet-seating-position", "0.5,-0.15,0.5")).split(",");
            carpetSeatingPosition = new Vector(Double.parseDouble(vectorString[0]), Double.parseDouble(vectorString[1]), Double.parseDouble(vectorString[2]));
        } catch (NumberFormatException e) {
            carpetSeatingPosition = new Vector(0.5, -0.15D, 0.5);
        }

        disabledWorlds = config.getStringList("disabled-worlds").stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @EventHandler
    private void onChairSit(@NotNull ChairSitEvent event) {
        Chair chair = event.getChair();
        if (chair.getLocation() == null) return;

        if (plugin.hasWorldGuard()) if (!plugin.getWorldGuardManager().validateSeating(chair, event.getPlayer())) {
            new MessageEvent(MessagePath.WORLDGUARD, event.getPlayer()).callEvent();
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
    private void onChairLeave(@NotNull ChairLeaveEvent event) {
        Chair chair = event.getChair();
        if (chair.getLocation() == null) return;

        Block block = chair.getLocation().getBlock();
        Player player = event.getPlayer();
        boolean flag = event.exitWhereFacing();

        Location exit = block.getLocation();

        clearPlayer(player);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        if (flag) exit = findExitPoint(player.getLocation(), block).getLocation();
        if (Util.surroundedBlock(block) || Util.nearLiquid(player.getLocation().getBlock()))
            exit = block.getRelative(BlockFace.UP).getLocation();
        exit.setDirection(player.getEyeLocation().getDirection());
        exit.add(0.5, 0, 0.5);
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
    private void onToss(@NotNull ChairTossEvent event) {
        Chair chair = event.getChair();
        if (chair.getLocation() == null) return;

        Block block = chair.getBlock();
        Player player = event.getPlayer();
        Entity attacker = event.getAttacker();
        TargetInfo info = event.getInfo();
        boolean silent = event.isSilent();

        ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player, false);
        leaveEvent.callEvent();

        if (!silent) {
            MessageEvent tossedMessageEvent;
            MessageEvent tosserMessageEvent = null;
            if (attacker instanceof Player playerEntity) {
                if (info != null && !info.isSprinting()) {
                    tossedMessageEvent = new MessageEvent(MessagePath.TOSSED, player,
                            Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), playerEntity.displayName()));

                    tosserMessageEvent = new MessageEvent(MessagePath.TOSSING, playerEntity,
                            Placeholder.component(PlaceHolder.SEATED.getPlaceholder(), player.displayName()));
                } else {
                    tossedMessageEvent = new MessageEvent(MessagePath.TOSSEDSPEED, player,
                            Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), playerEntity.displayName()));

                    tosserMessageEvent = new MessageEvent(MessagePath.TOSSINGSPEED, playerEntity,
                            Placeholder.component(PlaceHolder.SEATED.getPlaceholder(), player.displayName()));
                }
            } else {
                Component attackerName = attacker.customName();
                if (attackerName == null) {
                    attackerName = Component.text(attacker.getName());
                }

                tossedMessageEvent = new MessageEvent(MessagePath.TOSSED, player,
                        Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), attackerName));
            }
            tossedMessageEvent.callEvent();
            if (tosserMessageEvent != null) {
                tosserMessageEvent.callEvent();
            }
        }

        if (canToss) { // todo shouldn't this check be higher up?
            tossPlayer(block, player, attacker);
        }
    }

    @EventHandler
    private void onChairReplace(@NotNull ChairReplaceEvent event) {
        Chair chair = event.getChair();
        if (chair.getLocation() == null) return;

        Block block = chair.getBlock();
        Player player = event.getPlayer();
        Player seated = event.getReplaced();

        if (player.getUniqueId() == seated.getUniqueId()) return;

        ChairTossEvent tossEvent = new ChairTossEvent(chair, seated, player, null, true);
        tossEvent.callEvent();

        MessageEvent tossed = new MessageEvent(MessagePath.TOSSED, seated,
                Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), player.displayName()));
        tossed.callEvent();

        ChairSitEvent sitEvent = new ChairSitEvent(new Chair(player, block.getLocation()), player);
        sitEvent.callEvent();

        MessageEvent tosser = new MessageEvent(MessagePath.TOSSING, player,
                Placeholder.component(PlaceHolder.SEATED.getPlaceholder(), seated.displayName()));
        tosser.callEvent();
    }

    @EventHandler
    private void onChairCheck(@NotNull ChairCheckEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        if (!player.hasPermission("rfchairs.use")) {
            new MessageEvent(MessagePath.SEAT_NOPERMS, player).callEvent();
            return;
        }

        if (player.getLocation().distance(block.getLocation().add(0.5, 0, 0.5)) >= maxDistance) {
            new MessageEvent(MessagePath.TOOFAR, player).callEvent();
            return;
        }

        if (maxItemSit > 0 && item.getAmount() > maxItemSit) {
            new MessageEvent(MessagePath.TOOMANYITEMS, player).callEvent();
            return;
        }

        if (!Util.blockIsChair(block, chairs)) {
            ChairSitEvent sitEvent = new ChairSitEvent(new Chair(player, block.getLocation()), player);
            sitEvent.callEvent();
        } else {
            Chair chair = Util.getChairFromBlock(block, chairs);
            if (chair == null) return;
            if (chair.getFakeSeat() != null && chair.getFakeSeat().getPassengers().isEmpty()) {
                chair.setPlayer(player);
                chair.getFakeSeat().addPassenger(player);
                if (regenWhenSitting) {
                    player.addPotionEffect(regenEffect);
                }
                return;
            }
            if (player.hasPermission("rfchairs.priority") && priorityIfHasPerm) {
                ChairReplaceEvent replaceEvent = new ChairReplaceEvent(chair, chair.getPlayer(), player);
                replaceEvent.callEvent();
                return;
            }
            if (chair.getPlayer() == null) {
                return;
            }
            if (player.getUniqueId().equals(chair.getPlayer().getUniqueId())) {
                orienting.add(player.getUniqueId());
                Location loc = chair.getFakeSeat().getLocation();
                loc.setDirection(player.getEyeLocation().getDirection().setY(0));
                chair.getFakeSeat().remove();
                ArmorStand newFakeSeat = Util.generateFakeSeatDir(chair, loc.getDirection());
                chair.setFakeSeat(newFakeSeat);
                chair.getFakeSeat().addPassenger(player);
            } else {
                MessageEvent occupied = new MessageEvent(MessagePath.OCCUPIED, player,
                        Placeholder.component(PlaceHolder.PLAYER.getPlaceholder(), player.displayName()),
                        Placeholder.component(PlaceHolder.SEATED.getPlaceholder(), chair.getPlayer().displayName()));
                occupied.callEvent();
            }
        }
    }

    @EventHandler
    private void onRightClick(@NotNull PlayerInteractEvent event) {
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(event.getPlayer().getWorld())) return;
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

        if (BlockFilter.isStairsBlock(block.getType())
                || BlockFilter.isCarpetBlock(block.getType())
                || BlockFilter.isSlabBlock(block.getType())) {
            if (BlockFilter.isStairsBlock(block.getType())) {
                if (!Util.validStair(block)) return;
                else {
                    if (checkForSigns) {
                        if (!Util.validCouch(block)) {
                            if (!trapSeats) return;
                            if (!Util.throneChair(block)) {
                                new MessageEvent(MessagePath.NOSIGNS, player).callEvent();
                                return;
                            }
                        } else if (block.getRelative(BlockFace.UP).getType() != Material.AIR) {
                            return;
                        }
                    }
                    if (trapSeats && block.getRelative(BlockFace.UP).getType() != Material.AIR) {
                        if (!Util.throneChair(block)) return;
                    }
                }
            } else if (block.getRelative(BlockFace.UP).getType() != Material.AIR) return;

            if (BlockFilter.isSlabBlock(block.getType())) {
                if (!Util.validSlab(block)) {
                    return;
                }
            }

            if (BlockFilter.isCarpetBlock(block.getType())) {
                if (!Util.validCarpet(block)) {
                    return;
                }
            }

            if (event.getBlockFace() == BlockFace.DOWN) return;

            if (!player.hasPermission("rfchairs.use")) {
                new MessageEvent(MessagePath.SEAT_NOPERMS, player).callEvent();
                return;
            }

            new ChairCheckEvent(block, player).callEvent();
        }
    }

    @EventHandler
    private void onChangeGameMode(@NotNull PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;
        GameMode gameMode = event.getNewGameMode();
        Chair chair = chairMap.get(event.getPlayer().getUniqueId());
        if (gameMode == GameMode.SPECTATOR && chair != null) {
            clearChair(chair);
        }
    }

    @EventHandler
    private void onDismount(@NotNull EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Chair chair = chairMap.get(player.getUniqueId());

        if (chair == null || event.getDismounted().getType() != EntityType.ARMOR_STAND) return;
        if (leaving.contains(player.getUniqueId())) return;
        if (orienting.contains(player.getUniqueId())) {
            orienting.remove(player.getUniqueId());
            return;
        }

        if (tossed.containsKey(player.getUniqueId())) {
            TargetInfo info = tossed.get(player.getUniqueId());
            Entity attacker = info.getAttacker();
            if (info.hasSource() && info.livingSource()) {
                attacker = info.getLivingSource();
            }

            ChairTossEvent tossEvent = new ChairTossEvent(chair, player, attacker, info, false);
            tossEvent.callEvent();
            tossed.remove(player.getUniqueId());
            return;
        }
        leaving.add(player.getUniqueId());
    }

    @EventHandler
    private void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (leaving.contains(player.getUniqueId())) {
            ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chairMap.get(player.getUniqueId()), player, exitWhereFacing);
            leaveEvent.callEvent();
            leaving.remove(player.getUniqueId());
        }
    }

    @EventHandler
    private void takeDamage(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        Entity attacker = event.getDamager();
        ProjectileSource source = null;
        if (attacker instanceof Projectile projectile) {
            source = projectile.getShooter();
        }
        Chair chair = chairMap.get(player.getUniqueId());
        if (chair == null) {
            return;
        }

        if (event.getDamage() >= minDamage && canLaunch) {
            TargetInfo info;
            if (source == null) {
                info = new TargetInfo(attacker, event.getDamage());
            } else {
                info = new TargetInfo(source, attacker, event.getDamage());
            }
            tossed.put(player.getUniqueId(), info);
            if (chair.getFakeSeat() != null) {
                chair.getFakeSeat().removePassenger(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    // we want to act after the event may was canceled by protection plugins
    private void onBlockBreak(@NotNull BlockBreakEvent event) {
        chairs.stream()
                .filter(chair -> chair.getLocation() != null)
                .filter(chair -> Util.samePosition(event.getBlock(), chair.getLocation()))
                .findAny()
                .ifPresent(chair -> {
                    if (!event.getPlayer().hasPermission("rfchairs.break")) {
                        event.setCancelled(true);
                    } else { // eject player if block was broken. Note: the dismount event will handle the rest.
                        if (chair.getFakeSeat() != null) {
                            chair.getFakeSeat().removePassenger(event.getPlayer());
                        }
                    }
                });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void quit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Chair chair = chairMap.get(player.getUniqueId());
        if (chair != null) {
            ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player, exitWhereFacing);
            leaveEvent.callEvent();
        }
    }

    @EventHandler
    private void pistonExtend(@NotNull BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            for (Chair chair : chairs) {
                if (chair.getLocation() == null) continue;
                if (Util.samePosition(block, chair.getLocation())) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    private void pistonRetract(@NotNull BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            for (Chair chair : chairs) {
                if (chair.getLocation() == null) continue;
                if (Util.samePosition(block, chair.getLocation())) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    private void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Chair chair = chairMap.get(player.getUniqueId());
        if (chair != null) {
            ChairLeaveEvent leaveEvent = new ChairLeaveEvent(chair, player);
            leaveEvent.callEvent();
        }
    }

    /*
     * Sitting and Ejection Methods
     */

    private boolean sitPlayer(@NotNull Chair chair, @NotNull Player player) {
        if (chair.isOccupied()) return false;

        ArmorStand fakeSeat = chair.getFakeSeat();
        if (fakeSeat == null) fakeSeat = Util.generateFakeSeat(chair);

        fakeSeat.addPassenger(player);
        chair.setFakeSeat(fakeSeat);
        fakeSeats.add(fakeSeat.getUniqueId().toString());
        configManager.getData().set("UUIDs", fakeSeats);
        return true;
    }

    private void tossPlayer(@NotNull Block block, @NotNull Player player, @NotNull Entity entity) {
        Block exit = block;

        if (exitWhereFacing) {
            exit = findExitPoint(entity.getLocation(), block);
        }

        Location exitLoc = exit.getLocation().add(0.5, 0.5, 0.5);

        if (!canToss) {
            return;
        }

        if (!faceAttacker) {
            exitLoc.setPitch(player.getLocation().getPitch());
            exitLoc.setYaw(player.getLocation().getYaw());
        } else {
            exitLoc.setDirection(Util.getVectorDir(player.getLocation(), entity.getLocation()));
        }

        if (tossVelocity != 0) {
            double finalVelocity = tossVelocity;
            if (scaleVelocity && entity instanceof Player) {
                if (((Player) entity).isSprinting()) finalVelocity = tossVelocity * scaleAmount;
            }
            player.setVelocity(exitLoc.getDirection().multiply(finalVelocity));
        }
    }

    private Block findExitPoint(Location entity, Block block) {
        Block blockToCheck = Util.getBlockFromDirection(block, Util.getCardinalDirection(entity));
        boolean foundValidExit = Util.canFitPlayer(blockToCheck) && Util.safePlace(blockToCheck);

        String[] directions = {"north", "east", "south", "west"};
        for (String direction : directions) {
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
        if (toggled.isEmpty()) {
            configManager.getData().set("Toggled", new ArrayList<String>());
        }
        for (UUID id : toggled) {
            ids.add(id.toString());
        }
        plugin.getServer().getLogger().info("Saving " + ids.size() + " Players that had toggled off.");
        configManager.getData().set("Toggled", ids);
    }

    void loadToggled() {
        List<String> toggled = configManager.getData().getStringList("Toggled");
        if (toggled.isEmpty()) return;
        plugin.getServer().getLogger().info(toggled.size() + " Players had toggled off. Adding Them...");

        toggled.stream()
                .map(UUID::fromString)
                .forEach(this.toggled::add);

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
        for (Chair chair : chairs) clearChair(chair);
    }

    private void clearPlayer(@NotNull Player player) {
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
            plugin.getServer().getLogger().info("Detected " + fakes.size() + " leftover seats! Removing...");
            for (String fake : fakes) {
                UUID id = UUID.fromString(fake);
                Entity armorStand = plugin.getServer().getEntity(id);
                if (armorStand == null) continue;
                armorStand.remove();
            }
            configManager.getData().set("UUIDs", new ArrayList<UUID>());
        } else {
            plugin.getServer().getLogger().info("No fake seats remaining! Proceeding");
        }
    }

    public @NotNull List<@NotNull UUID> getToggled() {
        return toggled;
    }

    public Vector getStairSeatingPosition() {
        return stairSeatingPosition;
    }

    public Vector getSlabSeatingPosition() {
        return slabSeatingPosition;
    }

    public Vector getCarpetSeatingPosition() {
        return carpetSeatingPosition;
    }
}
