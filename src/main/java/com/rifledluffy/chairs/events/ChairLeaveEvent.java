package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChairLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Chair chair;
    private final Player player;
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

    static public HandlerList getHandlerList() {
        return handlers;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
