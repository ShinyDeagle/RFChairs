package com.rifledluffy.chairs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rifledluffy.chairs.chairs.Chair;

public class ChairLeaveEvent extends Event {
	
	private Chair chair;
	private Player player;
	private boolean exitWhereFacing = false;
	
	public ChairLeaveEvent(Chair chair, Player player, boolean flag) {
		this.chair = chair;
		this.player = player;
		this.exitWhereFacing = flag;
	}
	
	public ChairLeaveEvent(Chair chair, Player player) {
		this.chair = chair;
		this.player = player;
	}
	
	public Chair getChair() {
		return this.chair;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean exitWhereFacing() {
		return this.exitWhereFacing;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	static public HandlerList getHandlerList() {
		return handlers;
	}
}
