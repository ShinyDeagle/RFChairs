package com.rifledluffy.chairs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rifledluffy.chairs.chairs.Chair;

public class ChairReplaceEvent extends Event {
	
	Chair chair;
	Player replaced;
	Player player;
	
	public ChairReplaceEvent(Chair chair, Player replaced, Player player) {
		this.chair = chair;
		this.replaced = replaced;
		this.player = player;
	}
	
	public Chair getChair() {
		return this.chair;
	}
	
	public Player getReplaced() {
		return this.replaced;
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
