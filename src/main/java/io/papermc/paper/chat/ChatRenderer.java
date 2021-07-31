package io.papermc.paper.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * A chat renderer is responsible for rendering chat messages sent by {@link Player}s to the server.
 */
@FunctionalInterface
public interface ChatRenderer {
    /**
     * Renders a chat message. This will be called once for each receiving {@link Audience}.
     *
     * @param source the message source
     * @param sourceDisplayName the display name of the source player
     * @param message the chat message
     * @param viewer the receiving {@link Audience}
     * @return a rendered chat message
     */
    @NonNull
    Component render(@NonNull Player source, @NonNull Component sourceDisplayName, @NonNull Component message, @NonNull Audience viewer);

    /**
     * Create a new instance of the default {@link ChatRenderer}.
     *
     * @return a new {@link ChatRenderer}
     */
    @NonNull
    static ChatRenderer defaultRenderer() {
        return viewerUnaware((source, sourceDisplayName, message) -> {
            return Component.text("<").children(List.of(
                    sourceDisplayName,
                    Component.text("> "),
                    message));
        });
    }

    /**
     * Creates a new viewer-unaware {@link ChatRenderer}, which will render the chat message a single time,
     * displaying the same rendered message to every viewing {@link Audience}.
     *
     * @param renderer the viewer unaware renderer
     * @return a new {@link ChatRenderer}
     */
    @NonNull
    static ChatRenderer viewerUnaware(final @NonNull ViewerUnaware renderer) {
        return new ChatRenderer() {
            private Component message;

            @Override
            public @NonNull Component render(final @NonNull Player source, final @NonNull Component sourceDisplayName, final @NonNull Component message, final @NonNull Audience viewer) {
                if (this.message == null) {
                    this.message = renderer.render(source, sourceDisplayName, message);
                }
                return this.message;
            }
        };
    }

    /**
     * Similar to {@link ChatRenderer}, but without knowledge of the message viewer.
     *
     * @see ChatRenderer#viewerUnaware(ViewerUnaware)
     */
    interface ViewerUnaware {
        /**
         * Renders a chat message.
         *
         * @param source the message source
         * @param sourceDisplayName the display name of the source player
         * @param message the chat message
         * @return a rendered chat message
         */
        @NonNull
        Component render(@NonNull Player source, @NonNull Component sourceDisplayName, @NonNull Component message);
    }
}
