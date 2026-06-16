package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChairSitEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @NotNull Chair chair;
    private final @NotNull Player player;

    public ChairSitEvent(@NotNull Chair chair, @NotNull Player player) {
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
