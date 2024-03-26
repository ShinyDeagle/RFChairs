package com.rifledluffy.chairs.messages;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @NotNull MessageType type;
    private final @NotNull MessageConstruct construct;
    private final @NotNull Player player;
    private final @Nullable Entity other;
    private boolean cancelled;

    public MessageEvent(@NotNull MessageType type, @NotNull MessageConstruct construct, @NotNull Player player, @NotNull Player other) {
        this.type = type;
        this.construct = construct;
        this.player = player;
        this.other = other;
    }

    public MessageEvent(@NotNull MessageType type, @NotNull Player player, @NotNull Entity other) {
        this.type = type;
        this.construct = MessageConstruct.DEFENSIVE;
        this.player = player;
        this.other = other;
    }

    public MessageEvent(@NotNull MessageType type, @NotNull Player player) {
        this.type = type;
        this.construct = MessageConstruct.SINGLE;
        this.player = player;
        this.other = null;
    }

    static public @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull MessageType getType() {
        return this.type;
    }

    public @NotNull MessageConstruct getConstruct() {
        return this.construct;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @Nullable Entity getEntity() {
        return this.other;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
