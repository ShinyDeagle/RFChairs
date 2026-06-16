package com.rifledluffy.chairs.utility;

import com.rifledluffy.chairs.RFChairs;
import com.rifledluffy.chairs.chairs.BlockFilter;
import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Util {

    public static boolean validStair(@NotNull Block block) {
        if (!BlockFilter.validateStairs(block.getType())) return false;
        Stairs stair = (Stairs) block.getState().getBlockData();
        return stair.getHalf() != Bisected.Half.TOP;
    }

    public static boolean validSlab(@NotNull Block block) {
        if (!BlockFilter.validateSlab(block.getType())) return false;
        Slab slab = (Slab) block.getState().getBlockData();
        return slab.getType() == Slab.Type.BOTTOM;
    }

    public static boolean validCarpet(@NotNull Block block) {
        return BlockFilter.validateCarpet(block.getType());
    }

    private static boolean validatedChair(@NotNull Block block) {
        return BlockFilter.isStairsBlock(block.getType()) && validStair(block);
    }

    public static boolean playerIsSeated(@NotNull UUID uuid, @NotNull Map<@NotNull UUID, @NotNull Chair> chairMap) {
        Chair chair = chairMap.get(uuid);
        return chair != null;
    }

    public static boolean nearLiquid(@NotNull Block block) {
        return isLiquidOrMagma(block.getRelative(BlockFace.NORTH))
                || isLiquidOrMagma(block.getRelative(BlockFace.EAST))
                || isLiquidOrMagma(block.getRelative(BlockFace.SOUTH))
                || isLiquidOrMagma(block.getRelative(BlockFace.WEST));
    }

    public static boolean surroundedBlock(@NotNull Block block) {
        return !validExit(block.getRelative(BlockFace.NORTH))
                && !validExit(block.getRelative(BlockFace.EAST))
                && !validExit(block.getRelative(BlockFace.SOUTH))
                && !validExit(block.getRelative(BlockFace.WEST));
    }

    private static boolean validExit(@NotNull Block block) {
        Material type = block.getType();
        return type.isAir() || Tag.WALL_SIGNS.isTagged(type);
    }

    public static boolean isLiquidOrMagma(@NotNull Block block) {
        return block.getBlockData() instanceof Levelled || block.getRelative(BlockFace.DOWN).getType() == Material.MAGMA_BLOCK;
    }

    public static boolean validCouch(@NotNull Block block) {
        int validSides = 0;
        if (!validatedChair(block)) return false;
        Stairs stair = (Stairs) block.getState().getBlockData();

        Set<BlockFace> blockFaces = stair.getFaces();
        List<BlockFace> faces = new ArrayList<>(blockFaces);
        faces.remove(stair.getFacing());
        faces.remove(stair.getFacing().getOppositeFace());
        faces.remove(BlockFace.DOWN);
        faces.remove(BlockFace.UP);

        BlockFace side = faces.get(0);
        BlockFace otherSide = faces.get(1);

        if (!block.getRelative(BlockFace.UP).getType().isAir()) {
            return false;
        }

        if (validatedChair(block.getRelative(side))) {
            if (validSeat(block.getRelative(side), side, block)) {
                validSides++;
            }
        }
        if (validatedChair(block.getRelative(otherSide))) {
            if (validSeat(block.getRelative(otherSide), otherSide, block)) {
                validSides++;
            }
        }

        if (!validatedChair(block.getRelative(side))) {
            if (validSeat(block, side, block)) {
                validSides++;
            }
        }
        if (!validatedChair(block.getRelative(otherSide))) {
            if (validSeat(block, otherSide, block)) {
                validSides++;
            }
        }
        return validSides == 2;
    }

    public static boolean validSeat(@NotNull Block block, @NotNull BlockFace side, @NotNull Block original) {
        Stairs stair = (Stairs) block.getState().getBlockData();
        Stairs first = (Stairs) original.getState().getBlockData();
        if (stair.getFacing() != first.getFacing()) {
            return false;
        }
        if (block.getRelative(side).getBlockData() instanceof WallSign || block.getRelative(side).getBlockData() instanceof TrapDoor) {
            Directional sign = ((Directional) block.getRelative(side).getBlockData());
            return sign.getFacing() == side;
        } else if (validatedChair(block.getRelative(side))) {
            return validSeat(block.getRelative(side), side, original);
        }
        return false;
    }

    public static boolean sameSeat(@NotNull Player player, @NotNull Block block, @NotNull Map<@NotNull UUID, @NotNull Chair> chairMap) {
        Chair chair = chairMap.get(player.getUniqueId());
        if (chair == null || chair.getLocation() == null) {
            return false;
        }
        return chair.getLocation().equals(block.getLocation());
    }

    public static boolean samePosition(@NotNull Block block, @NotNull Location target) {
        return block.getX() == target.getX() && block.getY() == target.getY() && block.getZ() == target.getZ();
    }

    public static boolean throneChair(@NotNull Block block) {
        if (!(block.getBlockData() instanceof Stairs stairs)) {
            return false;
        }
        if (block.getRelative(BlockFace.UP).getBlockData() instanceof TrapDoor trapDoor) {
            if (!trapDoor.isOpen()) {
                return false;
            }
            return trapDoor.getFacing().getOppositeFace() == stairs.getFacing();
        }
        return false;
    }

    public static boolean blockIsChair(@NotNull Block block, @NotNull List<@Nullable Chair> chairs) {
        for (Chair chair : chairs) {
            if (chair == null || chair.getLocation() == null) {
                continue;
            }
            if (samePosition(block, chair.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public static @Nullable Chair getChairFromBlock(@NotNull Block block, @NotNull List<@Nullable Chair> chairs) {
        for (Chair chair : chairs) {
            if (chair == null || chair.getLocation() == null) {
                continue;
            }
            if (samePosition(block, chair.getLocation())) {
                return chair;
            }
        }
        return null;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable ArmorStand generateFakeSeat(@Nullable Chair chair) {
        if (chair == null) {
            return null;
        }
        Vector seatingPosition = new Vector(0.5, 0.3D, 0.5);
        Location seat = chair.getLocation();
        BlockFace facing = null;
        if (BlockFilter.isStairsBlock(chair.getBlock().getType())) {
            facing = ((Stairs) chair.getBlock().getState().getBlockData()).getFacing();
        }
        Location playerLoc = chair.getPlayer().getEyeLocation();
        playerLoc.setPitch(0);
        Vector vector;
        if (facing != null) {
            vector = getVectorFromFace(chair.getBlock(), facing.getOppositeFace());
        } else {
            vector = getVectorFromNearBlock(chair.getBlock(), playerLoc.getBlock());
        }

        if (BlockFilter.isStairsBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getStairSeatingPosition();
        }
        if (BlockFilter.isCarpetBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getCarpetSeatingPosition();
        }
        if (BlockFilter.isSlabBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getSlabSeatingPosition();
        }

        //Thank you VicenteRD and carlpoole!
        return seat.getWorld().spawn(
                seat.clone().add(seatingPosition).setDirection(vector),
                ArmorStand.class,
                stand -> {
                    stand.setVisible(false);
                    stand.setGravity(false);
                    stand.setInvulnerable(true);
                    stand.setMarker(true);
                    stand.setCollidable(false);
                });
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static @Nullable ArmorStand generateFakeSeatDir(@Nullable Chair chair, @NotNull Vector dir) {
        if (chair == null) {
            return null;
        }
        Vector seatingPosition = new Vector(0.5, 0.3D, 0.5);
        Location seat = chair.getLocation();
        Location playerLoc = chair.getPlayer().getEyeLocation();
        playerLoc.setPitch(0);

        if (BlockFilter.isStairsBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getStairSeatingPosition();
        }
        if (BlockFilter.isCarpetBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getCarpetSeatingPosition();
        }
        if (BlockFilter.isSlabBlock(chair.getBlock().getType())) {
            seatingPosition = RFChairs.getInstance().getChairManager().getSlabSeatingPosition();
        }

        //Thank you VicenteRD and carlpoole!
        return seat.getWorld().spawn(
                seat.clone().add(seatingPosition).setDirection(dir),
                ArmorStand.class,
                stand -> {
                    stand.setVisible(false);
                    stand.setGravity(false);
                    stand.setInvulnerable(true);
                    stand.setMarker(true);
                    stand.setCollidable(false);
                });
    }

    public static float getAbsoluteAngle(@NotNull Location loc) {
        float y = loc.getYaw();
        if (y < 0) {
            y += 360;
        }
        y %= 360;
        if (y <= 45 || y >= 315) return 0;
        if (y >= 45 && y <= 135) return 90;
        if (y >= 135 && y <= 225) return 180;
        return 270;
    }

    public static @NotNull String getCardinalDirection(@NotNull Location loc) {
        float y = loc.getYaw();
        if (y < 0) {
            y += 360;
        }
        y %= 360;
        if (y <= 45 || y >= 315) return "south";
        if (y >= 45 && y <= 135) return "west";
        if (y >= 135 && y <= 225) return "north";
        return "east";
    }

    public static Block getBlockFromDirection(@NotNull Block block, @NotNull String direction) {
        if (direction.equalsIgnoreCase("north")) {
            return block.getRelative(BlockFace.NORTH);
        } else if (direction.equalsIgnoreCase("west")) {
            return block.getRelative(BlockFace.WEST);
        } else if (direction.equalsIgnoreCase("east")) {
            return block.getRelative(BlockFace.EAST);
        } else if (direction.equalsIgnoreCase("south")) {
            return block.getRelative(BlockFace.SOUTH);
        } else if (direction.equalsIgnoreCase("down")) {
            return block.getRelative(BlockFace.DOWN);
        } else if (direction.equalsIgnoreCase("up")) {
            return block.getRelative(BlockFace.UP);
        }
        return block;
    }

    public static boolean canFitPlayer(@NotNull Block block) {
        return block.getType().isAir() && block.getRelative(BlockFace.UP).getType().isAir();
    }

    public static boolean safePlace(@NotNull Block block) {
        return !block.getRelative(BlockFace.DOWN).getType().isAir();
    }

    public static Vector getVectorDir(@NotNull Location caster, @NotNull Location target) {
        return target.clone().subtract(caster.toVector()).toVector();
    }

    public static Vector getVectorFromFace(@NotNull Block block, @NotNull BlockFace face) {
        Location blockLoc = block.getLocation();
        Location faceLoc = block.getRelative(face).getLocation();
        return getVectorDir(blockLoc, faceLoc);
    }

    public static Vector getVectorFromNearBlock(@NotNull Block block, @NotNull Block target) {
        return target.getLocation().toVector().subtract(block.getLocation().toVector());
    }
}
