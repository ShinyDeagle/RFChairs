package com.rifledluffy.chairs;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class TargetInfo {

	Player player;
	Entity attacker;
	ProjectileSource source;
	Double damage;
	boolean sprinting = false;
	
	TargetInfo(ProjectileSource source, Entity attacker, Double damage) {
		this.source = source;
		this.attacker = attacker;
		this.damage = damage;
	}
	
	TargetInfo(Entity attacker, Double damage) {
		this.source = null;
		if (attacker instanceof Player) {
			player = (Player) attacker;
			sprinting = player.isSprinting();
		}
		this.attacker = attacker;
		this.damage = damage;
	}
	
	Entity getEntity() {
		return attacker;
	}
	
	LivingEntity getLivingSource() {
		return (LivingEntity) source;
	}
	
	Block getBlockSource() {
		return ((BlockProjectileSource)source).getBlock();
	}
	
	Double getDamage() {
		return damage;
	}
	
	boolean isSprinting() {
		return this.sprinting;
	}
	
	boolean hasSource() {
		return source != null;
	}
	
	boolean livingSource() {
		return source != null && source instanceof LivingEntity;
	}
}
