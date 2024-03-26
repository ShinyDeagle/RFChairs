package com.rifledluffy.chairs.events;

import com.rifledluffy.chairs.TargetInfo;
import com.rifledluffy.chairs.chairs.Chair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChairTossEvent extends Event {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @NotNull Chair chair;
    private final @NotNull Player player;
    private final @NotNull Entity attacker;
    private final @Nullable TargetInfo info;
    private final boolean silent;

    public ChairTossEvent(@NotNull Chair chair, @NotNull Player player, @NotNull Entity attacker, @Nullable TargetInfo info, boolean flag) {
        this.chair = chair;
        this.player = player;
        this.attacker = attacker;
        this.info = info;
        this.silent = flag;
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

    public @NotNull Entity getAttacker() {
        return this.attacker;
    }

    public @Nullable TargetInfo getInfo() {
        return this.info;
    }

    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
