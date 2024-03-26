package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChairSitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Chair chair;
    Player player;

    public ChairSitEvent(Chair chair, Player player) {
        this.chair = chair;
        this.player = player;
    }

    static public HandlerList getHandlerList() {
        return handlers;
    }

    public Chair getChair() {
        return this.chair;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
