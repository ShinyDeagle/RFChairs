package com.rifledluffy.chairs.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final @NotNull MessagePath type;
    private @NotNull TagResolver @Nullable [] resolvers;
    private @NotNull Audience audience;
    private boolean cancelled;

    public MessageEvent(@NotNull MessagePath type, @NotNull Audience audience, @NotNull TagResolver @Nullable ... resolvers) {
        this.type = type;
        this.audience = audience;
        this.resolvers = resolvers;
    }

    static public @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull MessagePath getType() {
        return this.type;
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

    public @NotNull Audience getAudience() {
        return audience;
    }

    public void setAudience(@NotNull Audience audience) {
        this.audience = audience;
    }

    public @NotNull TagResolver @Nullable [] getResolvers() {
        return resolvers;
    }

    public void setResolvers(@NotNull TagResolver @Nullable [] replacingResolvers) {
        this.resolvers = replacingResolvers;
    }
}
