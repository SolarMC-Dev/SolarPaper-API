package org.bukkit.entity;

import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.EntityEffect;
import org.bukkit.Nameable;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.Metadatable;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.block.PistonMoveReaction;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.arim.omnibus.util.concurrent.ReactionStage;

/**
 * Represents a base entity in the world. <br>
 * <br>
 * <b>Async Teleportation</b> <br>
 * The {@code teleportAsync} and {@code teleportAsynchronously} methods
 * allow teleporting the entity to a location after the chunk at the destination
 * has been loaded asynchronously. These methods have similar semantics to the
 * async chunk loading methods in {@link World}. <br>
 * <br>
 * The {@code CompletableFuture} based methods guarantee that <i>non-async</i> dependent
 * operations will run on the main thread. The {@code CentralisedFuture} based methods,
 * whose method names are suffixed with "Asynchronously" to avoid conflicts, guarantee that
 * <i>sync</i> dependent operations will run on the main thread; importantly, they do not provide
 * the same guarantee as {@code CompletableFuture} based methods regarding <i>non-async</i> operations.
 */
public interface Entity extends Metadatable, CommandSender, Nameable, PersistentDataHolder, HoverEventSource<HoverEvent.ShowEntity> { // Solar - PersistentDataHolder and adventure
    /**
     * Gets the entity's current position
     *
     * @return a new copy of Location containing the position of this entity
     */
    public Location getLocation();

    /**
     * Stores the entity's current position in the provided Location object.
     * <p>
     * If the provided Location is null this method does nothing and returns
     * null.
     *
     * @param loc the location to copy into
     * @return The Location object provided or null
     */
    public Location getLocation(Location loc);

    /**
     * Sets this entity's velocity
     *
     * @param velocity New velocity to travel with
     */
    public void setVelocity(Vector velocity);

    /**
     * Gets this entity's current velocity
     *
     * @return Current traveling velocity of this entity
     */
    public Vector getVelocity();

    /**
     * Gets the entity's height
     *
     * @return height of entity
     */
    public double getHeight();

    /**
     * Gets the entity's width
     *
     * @return width of entity
     */
    public double getWidth();

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     */
    public boolean isOnGround();

    /**
     * Gets the current world this entity resides in
     *
     * @return World
     */
    public World getWorld();

    /**
     * Teleports this entity to the given location. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param location New location to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Location location);

    /**
     * Teleports this entity to the given location. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param location New location to teleport this entity to
     * @param cause The cause of this teleportation
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Location location, TeleportCause cause);

    /**
     * Teleports this entity to the target Entity. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param destination Entity to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Entity destination);

    /**
     * Teleports this entity to the target Entity. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param destination Entity to teleport this entity to
     * @param cause The cause of this teleportation
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Entity destination, TeleportCause cause);

    // Solar start - async teleport API
    /**
     * Loads/Generates the chunk asynchronously, and then teleports the entity when the chunk is ready.
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the destination
     * @return a future which yields {@code true} if the teleport is successful
     */
    CompletableFuture<Boolean> teleportAsync(@NonNull Location loc);

    /**
     * Loads/Generates the chunk asynchronously, and then teleports the entity when the chunk is ready.
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the destination
     * @return a future which yields {@code true} if the teleport is successful
     */
    ReactionStage<Boolean> teleportAsynchronously(@NonNull Location loc);

    /**
     * Loads/Generates the chunk asynchronously, and then teleports the entity when the chunk is ready.
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the destination
     * @param cause the teleportation cause
     * @return a future which yields {@code true} if the teleport is successful
     */
    CompletableFuture<Boolean> teleportAsync(@NonNull Location loc, @NonNull TeleportCause cause);

    /**
     * Loads/Generates the chunk asynchronously, and then teleports the entity when the chunk is ready.
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the destination
     * @param cause the teleportation cause
     * @return a future which yields {@code true} if the teleport is successful
     */
    ReactionStage<Boolean> teleportAsynchronously(@NonNull Location loc, @NonNull TeleportCause cause);
    // Solar end

    /**
     * Returns a list of entities within a bounding box centered around this
     * entity
     *
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return {@code List<Entity>} List of entities nearby
     */
    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z);

    /**
     * Returns a unique id for this entity
     *
     * @return Entity id
     */
    public int getEntityId();

    /**
     * Returns the entity's current fire ticks (ticks before the entity stops
     * being on fire).
     *
     * @return int fireTicks
     */
    public int getFireTicks();

    /**
     * Returns the entity's maximum fire ticks.
     *
     * @return int maxFireTicks
     */
    public int getMaxFireTicks();

    /**
     * Sets the entity's current fire ticks (ticks before the entity stops
     * being on fire).
     *
     * @param ticks Current ticks remaining
     */
    public void setFireTicks(int ticks);

    /**
     * Mark the entity's removal.
     */
    public void remove();

    /**
     * Returns true if this entity has been marked for removal.
     *
     * @return True if it is dead.
     */
    public boolean isDead();

    /**
     * Returns false if the entity has died or been despawned for some other
     * reason.
     *
     * @return True if valid.
     */
    public boolean isValid();

    /**
     * Gets the {@link Server} that contains this Entity
     *
     * @return Server instance running this Entity
     */
    public Server getServer();

    /**
     * Gets the primary passenger of a vehicle. For vehicles that could have
     * multiple passengers, this will only return the primary passenger.
     *
     * @return an entity
     * @deprecated entities may have multiple passengers, use
     * {@link #getPassengers()}
     */
    @Deprecated
    public Entity getPassenger();

    /**
     * Set the passenger of a vehicle.
     *
     * @param passenger The new passenger.
     * @return false if it could not be done for whatever reason
     * @deprecated entities may have multiple passengers, use
     * {@link #getPassengers()}
     */
    @Deprecated
    public boolean setPassenger(Entity passenger);

    /**
     * Gets a list of passengers of this vehicle.
     * <p>
     * The returned list will not be directly linked to the entity's current
     * passengers, and no guarantees are made as to its mutability.
     *
     * @return list of entities corresponding to current passengers.
     */
    public List<Entity> getPassengers();

    /**
     * Add a passenger to the vehicle.
     *
     * @param passenger The passenger to add
     * @return false if it could not be done for whatever reason
     */
    public boolean addPassenger(Entity passenger);

    /**
     * Remove a passenger from the vehicle.
     *
     * @param passenger The passenger to remove
     * @return false if it could not be done for whatever reason
     */
    public boolean removePassenger(Entity passenger);

    /**
     * Check if a vehicle has passengers.
     *
     * @return True if the vehicle has no passengers.
     */
    public boolean isEmpty();

    /**
     * Eject any passenger.
     *
     * @return True if there was a passenger.
     */
    public boolean eject();

    /**
     * Returns the distance this entity has fallen
     *
     * @return The distance.
     */
    public float getFallDistance();

    /**
     * Sets the fall distance for this entity
     *
     * @param distance The new distance.
     */
    public void setFallDistance(float distance);

    /**
     * Record the last {@link EntityDamageEvent} inflicted on this entity
     *
     * @param event a {@link EntityDamageEvent}
     */
    public void setLastDamageCause(EntityDamageEvent event);

    /**
     * Retrieve the last {@link EntityDamageEvent} inflicted on this entity.
     * This event may have been cancelled.
     *
     * @return the last known {@link EntityDamageEvent} or null if hitherto
     *     unharmed
     */
    public EntityDamageEvent getLastDamageCause();

    /**
     * Returns a unique and persistent id for this entity
     *
     * @return unique id
     */
    public UUID getUniqueId();

    /**
     * Gets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities.
     *
     * @return Age of entity
     */
    public int getTicksLived();

    /**
     * Sets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities. May not be less than one
     * tick.
     *
     * @param value Age of entity
     */
    public void setTicksLived(int value);

    /**
     * Performs the specified {@link EntityEffect} for this entity.
     * <p>
     * This will be viewable to all players near the entity.
     * <p>
     * If the effect is not applicable to this class of entity, it will not play.
     *
     * @param type Effect to play.
     */
    public void playEffect(EntityEffect type);

    /**
     * Get the type of the entity.
     *
     * @return The entity type.
     */
    public EntityType getType();

    /**
     * Returns whether this entity is inside a vehicle.
     *
     * @return True if the entity is in a vehicle.
     */
    public boolean isInsideVehicle();

    /**
     * Leave the current vehicle. If the entity is currently in a vehicle (and
     * is removed from it), true will be returned, otherwise false will be
     * returned.
     *
     * @return True if the entity was in a vehicle.
     */
    public boolean leaveVehicle();

    /**
     * Get the vehicle that this player is inside. If there is no vehicle,
     * null will be returned.
     *
     * @return The current vehicle.
     */
    public Entity getVehicle();

    /**
     * Sets whether or not to display the mob's custom name client side. The
     * name will be displayed above the mob similarly to a player.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @param flag custom name or not
     */
    public void setCustomNameVisible(boolean flag);

    /**
     * Gets whether or not the mob's custom name is displayed client side.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @return if the custom name is displayed
     */
    public boolean isCustomNameVisible();

    /**
     * Sets whether the entity has a team colored (default: white) glow.
     *
     * @param flag if the entity is glowing
     */
    void setGlowing(boolean flag);

    /**
     * Gets whether the entity is glowing or not.
     *
     * @return whether the entity is glowing
     */
    boolean isGlowing();

    /**
     * Sets whether the entity is invulnerable or not.
     * <p>
     * When an entity is invulnerable it can only be damaged by players in
     * creative mode.
     *
     * @param flag if the entity is invulnerable
     */
    public void setInvulnerable(boolean flag);

    /**
     * Gets whether the entity is invulnerable or not.
     *
     * @return whether the entity is
     */
    public boolean isInvulnerable();

    /**
     * Gets whether the entity is silent or not.
     *
     * @return whether the entity is silent.
     */
    public boolean isSilent();

    /**
     * Sets whether the entity is silent or not.
     * <p>
     * When an entity is silent it will not produce any sound.
     *
     * @param flag if the entity is silent
     */
    public void setSilent(boolean flag);

    /**
     * Returns whether gravity applies to this entity.
     *
     * @return whether gravity applies
     */
    boolean hasGravity();

    /**
     * Sets whether gravity applies to this entity.
     *
     * @param gravity whether gravity should apply
     */
    void setGravity(boolean gravity);

    /**
     * Gets the period of time (in ticks) before this entity can use a portal.
     *
     * @return portal cooldown ticks
     */
    int getPortalCooldown();

    /**
     * Sets the period of time (in ticks) before this entity can use a portal.
     *
     * @param cooldown portal cooldown ticks
     */
    void setPortalCooldown(int cooldown);

    /**
     * Returns a set of tags for this entity.
     * <br>
     * Entities can have no more than 1024 tags.
     *
     * @return a set of tags for this entity
     */
    Set<String> getScoreboardTags();

    /**
     * Add a tag to this entity.
     * <br>
     * Entities can have no more than 1024 tags.
     *
     * @param tag the tag to add
     * @return true if the tag was successfully added
     */
    boolean addScoreboardTag(String tag);

    /**
     * Removes a given tag from this entity.
     *
     * @param tag the tag to remove
     * @return true if the tag was successfully removed
     */
    boolean removeScoreboardTag(String tag);

    /**
     * Returns the reaction of the entity when moved by a piston.
     *
     * @return reaction
     */
    PistonMoveReaction getPistonMoveReaction();

    // Spigot start
    public class Spigot extends CommandSender.Spigot
    {

        /**
         * Returns whether this entity is invulnerable.
         *
         * @return True if the entity is invulnerable.
         */
        public boolean isInvulnerable()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    @Override
    Spigot spigot();
    // Spigot end

    // Paper start
    /**
     * Gets the location where this entity originates from.
     * <p>
     * This value can be null if the entity hasn't yet been added to the world.
     *
     * @return Location where entity originates or null if not yet added
     */
    Location getOrigin();

    /**
     * Returns whether this entity was spawned from a mob spawner.
     *
     * @return True if entity spawned from a mob spawner
     */
    boolean fromMobSpawner();

    /**
     * Gets the latest chunk an entity is currently or was in.
     *
     * @return The current, or most recent chunk if the entity is invalid (which may load the chunk)
     */
    Chunk getChunk();
    // Paper end
}
