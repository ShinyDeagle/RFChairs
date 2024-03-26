package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChairReplaceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Chair chair;
    Player replaced;
    Player player;

    public ChairReplaceEvent(Chair chair, Player replaced, Player player) {
        this.chair = chair;
        this.replaced = replaced;
        this.player = player;
    }

    static public HandlerList getHandlerList() {
        return handlers;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
