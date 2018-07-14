package com.rifledluffy.chairs.messages;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageEvent extends Event implements Cancellable {
	
	boolean cancelled;
	
	MessageType type;
	MessageConstruct construct;
	Player player;
	Entity other;
	
	public MessageEvent(MessageType type, MessageConstruct construct, Player player, Player other) {
		this.type = type;
		this.construct = construct;
		this.player = player;
		this.other = other;
	}
	
	public MessageEvent(MessageType type, Player player, Entity other) {
		this.type = type;
		this.construct = MessageConstruct.DEFENSIVE;
		this.player = player;
		this.other = other;
	}
	
	public MessageEvent(MessageType type, Player player) {
		this.type = type;
		this.construct = MessageConstruct.SINGLE;
		this.player = player;
		this.other = null;
	}
	
	public MessageType getType() {
		return this.type;
	}
	
	public MessageConstruct getConstruct() {
		return this.construct;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Entity getEntity() {
		return this.other;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	static public HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
