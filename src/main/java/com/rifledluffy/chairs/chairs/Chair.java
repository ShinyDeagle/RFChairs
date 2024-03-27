package com.rifledluffy.chairs.chairs;

import com.rifledluffy.chairs.RFChairs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Chair {
    private final BukkitRunnable clear;
    private final int clearID;

    private @Nullable Location location;
    private @Nullable Block chair;
    private ArmorStand fakeSeat = null;
    private @Nullable Player player;

    public Chair(@NotNull Player player, @NotNull Location location) {
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
                if (fakeSeat.getPassengers().isEmpty()) {
                    clear();
                }
            }

        };
        clearID = clear.runTaskTimer(RFChairs.getInstance(), 20, 20).getTaskId();
    }

    /*
     * Getters
     */

    public @Nullable ArmorStand getFakeSeat() {
        return fakeSeat;
    }

    public void setFakeSeat(@NotNull ArmorStand armorStand) {
        fakeSeat = armorStand;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public void setPlayer(@NotNull Player player) {
        this.player = player;
    }

    public @Nullable Block getBlock() {
        if (location == null) {
            return null;
        } else {
            return location.getBlock();
        }
    }

    public @Nullable BlockState getBlockState() {
        if (chair == null) {
            return null;
        } else {
            return chair.getState();
        }
    }

    public @Nullable BlockData getBlockData() {
        if (chair == null) {
            return null;
        } else {
            return chair.getState().getBlockData();
        }
    }

    public @Nullable BlockFace getFacing() {
        if (chair != null && BlockFilter.isStairsBlock(chair.getType())) {
            return ((Stairs) chair.getState().getBlockData()).getFacing();
        }
        return null;
    }

    /*
     * Setters
     */

    public @Nullable Location getLocation() {
        return location;
    }

    /*
     * Methods
     */

    public boolean isOccupied() {
        return !(fakeSeat == null) && !fakeSeat.isEmpty();
    }

    public void clear() {
        if (fakeSeat != null) {
            fakeSeat.remove();
        }
        if (player != null) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }
        chair = null;
        location = null;
        fakeSeat = null;
        player = null;
        if (Bukkit.getScheduler().isCurrentlyRunning(clearID)) {
            clear.cancel();
        }
    }
}
