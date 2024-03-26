package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChairCheckEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @Nullable Chair chair;
    private final @Nullable Block block;
    private final @NotNull Player player;

    public ChairCheckEvent(@NotNull Chair chair, @NotNull Player player) {
        this.chair = chair;
        this.block = null;
        this.player = player;
    }

    public ChairCheckEvent(@NotNull Block block, @NotNull Player player) {
        this.chair = null;
        this.block = block;
        this.player = player;
    }

    static public @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public @Nullable Chair getChair() {
        return this.chair;
    }

    public @Nullable Block getBlock() {
        return this.block;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
