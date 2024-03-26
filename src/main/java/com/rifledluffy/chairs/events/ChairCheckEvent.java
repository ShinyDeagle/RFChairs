package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChairCheckEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Chair chair;
    Block block;
    Player player;

    public ChairCheckEvent(Chair chair, Player player) {
        this.chair = chair;
        this.player = player;
    }

    public ChairCheckEvent(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    static public HandlerList getHandlerList() {
        return handlers;
    }

    public Chair getChair() {
        return this.chair;
    }

    public Block getBlock() {
        return this.block;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
