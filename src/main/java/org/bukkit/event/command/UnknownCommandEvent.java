package org.bukkit.event.command;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Thrown when a player executes a command that is not defined
 */
public class UnknownCommandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private CommandSender sender;
    private String commandLine;
    private String message;

    public UnknownCommandEvent(final CommandSender sender, final String commandLine, final String message) {
        super(false);
        this.sender = sender;
        this.commandLine = commandLine;
        this.message = message;
    }

    /**
     * Gets the CommandSender or ConsoleCommandSender
     * <p>
     *
     * @return Sender of the command
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the command that was send
     * <p>
     *
     * @return Command sent
     */
    public String getCommandLine() {
        return commandLine;
    }

    /**
     * Gets message that will be returned
     * <p>
     *
     * @return Unknown command message
     */
    @Nullable
    public String getMessage() {
        return message;
    }


    /**
     * Sets message that will be returned
     * <p>
     * Set to null to avoid any message being sent
     *
     * @param message the message to be returned, or null
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

