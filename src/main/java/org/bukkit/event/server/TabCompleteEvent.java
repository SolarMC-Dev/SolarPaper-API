package org.bukkit.event.server;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link CommandSender} of any description (ie: player or
 * console) attempts to tab complete.
 */
public class TabCompleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    //
    private final CommandSender sender;
    private final String buffer;
    private List<String> completions;
    private boolean cancelled;

    public TabCompleteEvent(CommandSender sender, String buffer, List<String> completions) {
        // Paper start
        this(sender, buffer, completions, sender instanceof org.bukkit.command.ConsoleCommandSender || buffer.startsWith("/"), null);
    }
    public TabCompleteEvent(CommandSender sender, String buffer, List<String> completions, boolean isCommand, org.bukkit.Location location) {
        this.isCommand = isCommand;
        this.loc = location;
        // Paper end
        Validate.notNull(sender, "sender");
        Validate.notNull(buffer, "buffer");
        Validate.notNull(completions, "completions");

        this.sender = sender;
        this.buffer = buffer;
        this.completions = completions;
    }

    /**
     * Get the sender completing this command.
     *
     * @return the {@link CommandSender} instance
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Return the entire buffer which formed the basis of this completion.
     *
     * @return command buffer, as entered
     */
    public String getBuffer() {
        return buffer;
    }

    /**
     * The list of completions which will be offered to the sender, in order.
     * This list is mutable and reflects what will be offered.
     *
     * @return a list of offered completions
     */
    public List<String> getCompletions() {
        return completions;
    }

    // Paper start
    private final boolean isCommand;
    private final org.bukkit.Location loc;
    /**
     * @return True if it is a command being tab completed, false if it is a chat message.
     */
    public boolean isCommand() {
        return isCommand;
    }

    /**
     * @return The position looked at by the sender, or null if none
     */
    public org.bukkit.Location getLocation() {
        return loc;
    }
    // Paper end

    /**
     * Set the completions offered, overriding any already set.
     *
     * The passed collection will be cloned to a new List. You must call {{@link #getCompletions()}} to mutate from here
     *
     * @param completions the new completions
     */
    public void setCompletions(List<String> completions) {
        Validate.notNull(completions);
        this.completions = new ArrayList<>(completions); // Paper
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
