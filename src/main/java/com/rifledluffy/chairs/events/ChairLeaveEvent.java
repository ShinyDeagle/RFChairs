package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChairLeaveEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @NotNull Chair chair;
    private final @NotNull Player player;
    private boolean exitWhereFacing = false;

    public ChairLeaveEvent(@NotNull Chair chair, @NotNull Player player, boolean flag) {
        this.chair = chair;
        this.player = player;
        this.exitWhereFacing = flag;
    }

    public ChairLeaveEvent(@NotNull Chair chair, @NotNull Player player) {
        this.chair = chair;
        this.player = player;
    }

    static public @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull Chair getChair() {
        return this.chair;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public boolean exitWhereFacing() {
        return this.exitWhereFacing;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
