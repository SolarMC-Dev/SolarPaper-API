package com.destroystokyo.paper.event.entity;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *  Fired when lightning strikes an entity
 */
public class EntityZapEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final LightningStrike bolt;
    private final Entity replacementEntity;

    public EntityZapEvent(final Entity entity, @NonNull final LightningStrike bolt, @NonNull final Entity replacementEntity) {
        super(entity);
        Validate.notNull(bolt);
        Validate.notNull(replacementEntity);
        this.bolt = bolt;
        this.replacementEntity = replacementEntity;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the lightning bolt that is striking the entity.
     * @return The lightning bolt responsible for this event
     */
    @NonNull
    public LightningStrike getBolt() {
        return bolt;
    }

    /**
     * Gets the entity that will replace the struck entity.
     * @return The entity that will replace the struck entity
     */
    @NonNull
    public Entity getReplacementEntity() {
        return replacementEntity;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
