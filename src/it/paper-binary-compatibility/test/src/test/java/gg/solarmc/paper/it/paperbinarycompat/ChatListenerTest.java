package gg.solarmc.paper.it.paperbinarycompat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ChatListenerTest {

    private final ChatListener listener = new ChatListener();
    private final AsyncPlayerChatEvent chatEvent = newEvent();

    private AsyncPlayerChatEvent newEvent() {
        return new AsyncPlayerChatEvent(true, mock(Player.class), "message", Set.of());
    }

    @Test
    public void cancelChat() {
        var event = newEvent();
        assertFalse(event.isCancelled());
        listener.cancelChat(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void renderChat() {
        Component rendered = Component.text("rendered value");
        listener.renderChat(chatEvent, rendered);
        assertEquals(rendered, chatEvent.renderer().render(
                mock(Player.class), Component.text("display name"), Component.text("message"), mock(Audience.class)));
    }

    @Test
    public void renderChatViewerUnaware() {
        Component rendered = Component.text("rendered value");
        listener.renderChatViewerUnaware(chatEvent, rendered);
        assertEquals(rendered, chatEvent.renderer().render(
                mock(Player.class), Component.text("display name"), Component.text("message"), mock(Audience.class)));
    }

    @Test
    public void obtainRenderer(@Mock ChatRenderer renderer) {
        chatEvent.renderer(renderer);
        assertEquals(renderer, listener.obtainRenderer(chatEvent));
    }
}
