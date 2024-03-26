package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.TargetInfo;
import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChairTossEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Chair chair;
    Player player;
    Entity attacker;
    TargetInfo info;
    boolean silent;

    public ChairTossEvent(Chair chair, Player player, Entity attacker, TargetInfo info, boolean flag) {
        this.chair = chair;
        this.player = player;
        this.attacker = attacker;
        this.info = info;
        this.silent = flag;
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

    public Entity getAttacker() {
        return this.attacker;
    }

    public TargetInfo getInfo() {
        return this.info;
    }

    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
