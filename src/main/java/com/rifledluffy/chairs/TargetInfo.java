package com.rifledluffy.chairs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetInfo {
    private final @NotNull Entity attacker;
    private final @Nullable ProjectileSource source;
    private final double damage;
    private boolean sprinting = false;

    protected TargetInfo(@NotNull ProjectileSource source, @NotNull Entity attacker, double damage) {
        this.source = source;
        this.attacker = attacker;
        this.damage = damage;
    }

    protected TargetInfo(@NotNull Entity attacker, double damage) {
        this.source = null;
        if (attacker instanceof Player player) {
            sprinting = player.isSprinting();
        }
        this.attacker = attacker;
        this.damage = damage;
    }

    protected @NotNull Entity getAttacker() {
        return attacker;
    }

    protected LivingEntity getLivingSource() {
        return (LivingEntity) source;
    }

    protected double getDamage() {
        return damage;
    }

    protected boolean isSprinting() {
        return this.sprinting;
    }

    protected boolean hasSource() {
        return source != null;
    }

    protected boolean livingSource() {
        return source instanceof LivingEntity;
    }
}
