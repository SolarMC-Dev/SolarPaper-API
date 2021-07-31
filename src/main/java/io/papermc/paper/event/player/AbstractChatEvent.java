package io.papermc.paper.event.player;

import io.papermc.paper.chat.ChatRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * An abstract implementation of a chat event, handling shared logic.
 */
public abstract class AbstractChatEvent extends PlayerEvent implements Cancellable {

    private ChatRenderer renderer;

    AbstractChatEvent(final boolean async, final Player player) {
        super(player, async);
    }

    /**
     * Sets the chat renderer.
     *
     * @param renderer the chat renderer
     * @throws NullPointerException if {@code renderer} is {@code null}
     */
    public final void renderer(final @NonNull ChatRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer);
    }

    /**
     * Gets the chat renderer.
     *
     * @return the chat renderer
     */
    @NonNull
    public final ChatRenderer renderer() {
        if (this.renderer == null) {
            this.renderer = ChatRenderer.defaultRenderer();
        }
        return this.renderer;
    }

    /*
    Having the setCancelled and isCancelled methods be implemented down the hierarchy
    is binary compatible with Paper's placement of them in this class. See IT for proof.
     */
}
