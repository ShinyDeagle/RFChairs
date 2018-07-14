package com.rifledluffy.chairs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rifledluffy.chairs.chairs.Chair;

public class ChairSitEvent extends Event {
	
	Chair chair;
	Player player;
	
	public ChairSitEvent(Chair chair, Player player) {
		this.chair = chair;
		this.player = player;
	}
	
	public Chair getChair() {
		return this.chair;
	}
	
	public Player getPlayer() {
		return this.player;
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
