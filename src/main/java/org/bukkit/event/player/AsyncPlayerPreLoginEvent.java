package org.bukkit.event.player;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Stores details for players attempting to log in.
 * <p>
 * This event is asynchronous, and not run using main thread.
 */
public class AsyncPlayerPreLoginEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Result result;
    private Component message; // Solar
    private final String name;
    private final InetAddress ipAddress;
    private final UUID uniqueId;

    @Deprecated
    public AsyncPlayerPreLoginEvent(final String name, final InetAddress ipAddress) {
        this(name, ipAddress, null);
    }

    public AsyncPlayerPreLoginEvent(final String name, final InetAddress ipAddress, final UUID uniqueId) {
        // Paper start
        this(name, ipAddress, uniqueId, Bukkit.createProfile(uniqueId, name));
    }
    private PlayerProfile profile;

    /**
     * Gets the PlayerProfile of the player logging in
     * @return The Profile
     */
    public PlayerProfile getPlayerProfile() {
        return profile;
    }

    /**
     * Changes the PlayerProfile the player will login as
     * @param profile The profile to use
     */
    public void setPlayerProfile(PlayerProfile profile) {
        this.profile = profile;
    }

    public AsyncPlayerPreLoginEvent(final String name, final InetAddress ipAddress, final UUID uniqueId, PlayerProfile profile) {
        super(true);
        this.profile = profile;
        // Paper end
        this.result = Result.ALLOWED;
        this.message = Component.empty(); // Solar
        this.name = name;
        this.ipAddress = ipAddress;
        this.uniqueId = uniqueId;
    }

    /**
     * Gets the current result of the login, as an enum
     *
     * @return Current Result of the login
     */
    public Result getLoginResult() {
        return result;
    }

    /**
     * Gets the current result of the login, as an enum
     *
     * @return Current Result of the login
     * @deprecated This method uses a deprecated enum from {@link
     *     PlayerPreLoginEvent}
     * @see #getLoginResult()
     */
    @Deprecated
    public PlayerPreLoginEvent.Result getResult() {
        return result == null ? null : result.old();
    }

    /**
     * Sets the new result of the login, as an enum
     *
     * @param result New result to set
     */
    public void setLoginResult(final Result result) {
        this.result = Objects.requireNonNull(result, "result"); // Solar - no tolerance for null
    }

    /**
     * Sets the new result of the login, as an enum
     *
     * @param result New result to set
     * @deprecated This method uses a deprecated enum from {@link
     *     PlayerPreLoginEvent}
     * @see #setLoginResult(Result)
     */
    @Deprecated
    public void setResult(final PlayerPreLoginEvent.Result result) {
        this.result = Result.valueOf(result.name()); // Solar - no tolerance for null
    }

    // Solar start - use adventure
    private String serialize(Component message) {
        return LegacyComponentSerializer.legacySection().serialize(message);
    }

    private Component deserialize(String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message);
    }

    /**
     * Gets the current kick message that will be used if getResult() !=
     * Result.ALLOWED
     *
     * @return Current kick message
     */
    public @NonNull Component kickMessage() {
        return message;
    }

    /**
     * Sets the kick message to display if getResult() != Result.ALLOWED
     *
     * @param message New kick message
     */
    public void kickMessage(@NonNull Component message) {
        this.message = Objects.requireNonNull(message, "message");
    }

    /**
     * Gets the current kick message that will be used if getResult() !=
     * Result.ALLOWED
     *
     * @return Current kick message
     * @deprecated Use the adventure {@link #kickMessage()}
     */
    @Deprecated
    public String getKickMessage() {
        return serialize(message);
    }

    /**
     * Sets the kick message to display if getResult() != Result.ALLOWED
     *
     * @param message New kick message
     * @deprecated Use the adventure {@link #kickMessage(Component)}
     */
    @Deprecated
    public void setKickMessage(final String message) {
        Objects.requireNonNull(message, "message");
        kickMessage(deserialize(message));
    }

    /**
     * Allows the player to log in
     */
    public void allow() {
        result = Result.ALLOWED;
        message = Component.empty();
    }

    /**
     * Disallows the player from logging in, with the given reason
     *
     * @param result New result for disallowing the player
     * @param message Kick message to display to the user
     */
    public void disallow(@NonNull final Result result, @NonNull final Component message) {
        this.result = Objects.requireNonNull(result, "result");
        this.message = Objects.requireNonNull(message, "message");
    }

    /**
     * Disallows the player from logging in, with the given reason
     *
     * @param result New result for disallowing the player
     * @param message Kick message to display to the user
     * @deprecated Use the adventure {@link #disallow(AsyncPlayerPreLoginEvent.Result, Component)}
     */
    @Deprecated
    public void disallow(final Result result, final String message) {
        disallow(result, deserialize(message));
    }
    // Solar end

    /**
     * Disallows the player from logging in, with the given reason
     *
     * @param result New result for disallowing the player
     * @param message Kick message to display to the user
     * @deprecated This method uses a deprecated enum from {@link
     *     PlayerPreLoginEvent}
     * @see #disallow(Result, String)
     */
    @Deprecated
    public void disallow(final PlayerPreLoginEvent.Result result, final String message) {
        disallow(Result.valueOf(result.name()), deserialize(message)); // Solar
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player IP address.
     *
     * @return The IP address
     */
    public InetAddress getAddress() {
        return ipAddress;
    }

    /**
     * Gets the player's unique ID.
     *
     * @return The unique ID
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Basic kick reasons for communicating to plugins
     */
    public enum Result {

        /**
         * The player is allowed to log in
         */
        ALLOWED,
        /**
         * The player is not allowed to log in, due to the server being full
         */
        KICK_FULL,
        /**
         * The player is not allowed to log in, due to them being banned
         */
        KICK_BANNED,
        /**
         * The player is not allowed to log in, due to them not being on the
         * white list
         */
        KICK_WHITELIST,
        /**
         * The player is not allowed to log in, for reasons undefined
         */
        KICK_OTHER;

        @Deprecated
        private PlayerPreLoginEvent.Result old() {
            return PlayerPreLoginEvent.Result.valueOf(name());
        }
    }
}
