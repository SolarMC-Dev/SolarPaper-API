package io.papermc.paper.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * An event fired when a {@link Player} sends a chat message to the server.
 */
public abstract class AsyncChatEvent extends AbstractChatEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    protected AsyncChatEvent(Player player, boolean async) {
        super(async, player);
    }

    @Override
    public final HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
