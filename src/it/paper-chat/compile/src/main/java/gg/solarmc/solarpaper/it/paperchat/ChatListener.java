package gg.solarmc.solarpaper.it.paperchat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChatListener implements Listener {

    public void cancelChat(AsyncChatEvent chatEvent) {
        if (chatEvent.isCancelled()) {
            throw new IllegalStateException("Already cancelled");
        }
        chatEvent.setCancelled(true);
    }

    public void renderChat(AsyncChatEvent chatEvent, Component result) {
        chatEvent.renderer(new ChatRenderer() {
            @Override
            public @NonNull Component render(@NonNull Player source, @NonNull Component sourceDisplayName,
                                             @NonNull Component message, @NonNull Audience viewer) {
                return result;
            }
        });
    }

    public void renderChatViewerUnaware(AsyncChatEvent chatEvent, Component result) {
        chatEvent.renderer(ChatRenderer.viewerUnaware(new ChatRenderer.ViewerUnaware() {
            @Override
            public @NonNull Component render(@NonNull Player source, @NonNull Component sourceDisplayName,
                                             @NonNull Component message) {
                return result;
            }
        }));
    }

    public ChatRenderer obtainRenderer(AsyncChatEvent chatEvent) {
        return chatEvent.renderer();
    }
}
