package org.bukkit;

import java.io.File;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.arim.omnibus.util.concurrent.ReactionStage;

/**
 * Represents a world, which may contain entities, chunks and blocks. <br>
 * <br>
 * <b>Async Chunk Loading</b> <br>
 * The {@code getChunkAtAsync} methods, including all various overloads,
 * and some variants of method names, support loading a chunk asynchronously.
 * Use these methods when you wish to let the server decide when best to load
 * the chunk with an eye to performance. <br>
 * <br>
 * Strictly speaking, these methods makes no guarantee as to how fast requested chunks
 * will load. <br>
 * <br>
 * All the methods provide some sort of means for running a callback on the main thread
 * which will execute when the chunk is ready. The {@code Consumer} based methods run
 * the given parameter on the main thread. The {@code CompletableFuture} based methods
 * guarantee that <i>non-async</i> dependent operations will run on the main thread.
 * Finally, {@code CentralisedFuture} based methods, whose method names are suffixed with
 * "Asynchronously" to avoid conflicts, guarantee that <i>sync</i> dependent operations will run
 * on the main thread; importantly, they do not provide the same guarantee as {@code CompletableFuture}
 * based methods regarding <i>non-async</i> operations. <br>
 * <br>
 * All these methods support a means of hinting that the chunk need not be generated with an optional
 * {@code gen} parameter. This hint may be ignored by some implementations. Moreover, all these methods
 * allow an additional hint that the chunk be loaded somewhat more urgently, with the "Urgently"
 * method name suffix. <br>
 * <br>
 * The chunk loading methods accepting integer coordinates use chunk coordinates, not block coordinates.
 * Chunk coordinates are the same as the block coordinates divided by 16 and then the result floored.
 */
public interface World extends PluginMessageRecipient, Metadatable {

    // Paper start
    /**
     * @return The amount of Entities in this world
     */
    int getEntityCount();

    /**
     * @return The amount of Tile Entities in this world
     */
    int getTileEntityCount();

    /**
     * @return The amount of Tickable Tile Entities in this world
     */
    int getTickableTileEntityCount();

    /**
     * @return The amount of Chunks in this world
     */
    int getChunkCount();

    /**
     * @return The amount of Players in this world
     */
    int getPlayerCount();
    // Paper end

    /**
     * Gets the {@link Block} at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Block at the given coordinates
     * @see #getBlockTypeIdAt(int, int, int) Returns the current type ID of
     *     the block
     */
    public Block getBlockAt(int x, int y, int z);

    /**
     * Gets the {@link Block} at the given {@link Location}
     *
     * @param location Location of the block
     * @return Block at the given location
     * @see #getBlockTypeIdAt(org.bukkit.Location) Returns the current type ID
     *     of the block
     */
    public Block getBlockAt(Location location);

    // Paper start
    /**
     * Gets the {@link Block} at the given block key
     *
     * @param key The block key. See {@link Block#getBlockKey()}
     * @return Block at the key
     * @see Location#toBlockKey()
     * @see Block#getBlockKey()
     */
    public default Block getBlockAtKey(long key) {
        int x = (int) ((key << 37) >> 37);
        int y = (int) (key >>> 54);
        int z = (int) ((key << 10) >> 37);
        return getBlockAt(x, y, z);
    }
    /**
     * Gets the {@link Location} at the given block key
     *
     * @param key The block key. See {@link Location#toBlockKey()}
     * @return Location at the key
     * @see Location#toBlockKey()
     * @see Block#getBlockKey()
     */
    public default Location getLocationAtKey(long key) {
        int x = (int) ((key << 37) >> 37);
        int y = (int) (key >>> 54);
        int z = (int) ((key << 10) >> 37);
        return new Location(this, x, y, z);
    }
    // Paper end

    /**
     * Gets the block type ID at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Type ID of the block at the given coordinates
     * @see #getBlockAt(int, int, int) Returns a live Block object at the
     *     given location
     * @deprecated Magic value
     */
    @Deprecated
    public int getBlockTypeIdAt(int x, int y, int z);

    /**
     * Gets the block type ID at the given {@link Location}
     *
     * @param location Location of the block
     * @return Type ID of the block at the given location
     * @see #getBlockAt(org.bukkit.Location) Returns a live Block object at
     *     the given location
     * @deprecated Magic value
     */
    @Deprecated
    public int getBlockTypeIdAt(Location location);

    /**
     * Gets the y coordinate of the lowest block at this position such that the
     * block and all blocks above it are transparent for lighting purposes.
     *
     * @param x X-coordinate of the blocks
     * @param z Z-coordinate of the blocks
     * @return Y-coordinate of the described block
     */
    public int getHighestBlockYAt(int x, int z);

    /**
     * Gets the y coordinate of the lowest block at the given {@link Location}
     * such that the block and all blocks above it are transparent for lighting
     * purposes.
     *
     * @param location Location of the blocks
     * @return Y-coordinate of the highest non-air block
     */
    public int getHighestBlockYAt(Location location);

    /**
     * Gets the lowest block at the given coordinates such that the block and
     * all blocks above it are transparent for lighting purposes.
     *
     * @param x X-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Highest non-empty block
     */
    public Block getHighestBlockAt(int x, int z);

    /**
     * Gets the lowest block at the given {@link Location} such that the block
     * and all blocks above it are transparent for lighting purposes.
     *
     * @param location Coordinates to get the highest block
     * @return Highest non-empty block
     */
    public Block getHighestBlockAt(Location location);

    /**
     * Gets the {@link Chunk} at the given coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Chunk at the given coordinates
     */
    public Chunk getChunkAt(int x, int z);

    /**
     * Gets the {@link Chunk} at the given {@link Location}
     *
     * @param location Location of the chunk
     * @return Chunk at the given location
     */
    public Chunk getChunkAt(Location location);

    /**
     * Gets the {@link Chunk} that contains the given {@link Block}
     *
     * @param block Block to get the containing chunk from
     * @return The chunk that contains the given block
     */
    public Chunk getChunkAt(Block block);

    // Paper start
    /**
     * Gets the chunk at the specified chunk key, which is the X and Z packed into a long.
     *
     * See {@link Chunk#getChunkKey()} for easy access to the key, or you may calculate it as:
     * long chunkKey = (long) chunkX &amp; 0xffffffffL | ((long) chunkZ &amp; 0xffffffffL) &gt;&gt; 32;
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return The chunk at the specified key
     */
    public default Chunk getChunkAt(long chunkKey) {
        return getChunkAt((int) chunkKey, (int) (chunkKey >> 32));
    }

    /**
     * Checks if a {@link Chunk} has been generated at the specified chunk key,
     * which is the X and Z packed into a long.
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return true if the chunk has been generated, otherwise false
     */
    public default boolean isChunkGenerated(long chunkKey) {
        return isChunkGenerated((int) chunkKey, (int) (chunkKey >> 32));
    }

    /**
     * Checks if a {@link Chunk} has been generated at the given coordinates.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk has been generated, otherwise false
     */
    public boolean isChunkGenerated(int x, int z);
    // Paper end

    // Solar start - update async chunks API
    /**
     * This is the Legacy API before Java 8 was supported. Java 8 Consumer is provided,
     * as well as future support.
     *
     * Used by {@link World#getChunkAtAsync(Location,ChunkLoadCallback)} methods
     * to request a {@link Chunk} to be loaded, with this callback receiving
     * the chunk when it is finished.
     *
     * This callback will be executed on synchronously on the main thread.
     *
     * Timing and order this callback is fired is intentionally not defined and
     * and subject to change.
     *
     * @deprecated Use either the Future or the Consumer based methods
     */
    @Deprecated
    interface ChunkLoadCallback {
        void onLoad(Chunk chunk);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     * @deprecated Use either the Future or the Consumer based methods
     */
    @Deprecated
    void getChunkAtAsync(int x, int z, @NonNull ChunkLoadCallback cb);

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param location Location of the chunk
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     * @deprecated Use either the Future or the Consumer based methods
     */
    @Deprecated
    void getChunkAtAsync(@NonNull Location location, @NonNull ChunkLoadCallback cb);

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     * deprecated Use either the Future or the Consumer based methods
     */
    @Deprecated
    void getChunkAtAsync(@NonNull Block block, @NonNull ChunkLoadCallback callback);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(int x, int z, @NonNull Consumer<Chunk> callback);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param gen Should we generate a chunk if it doesn't exists or not
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(int x, int z, boolean gen, @NonNull Consumer<Chunk> callback);

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param loc the location at which to load the cunk
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(@NonNull Location loc, @NonNull Consumer<Chunk> callback);

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param loc the location at which to load the cunk
     * @param gen whether to generate the chunk
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(@NonNull Location loc, boolean gen, @NonNull Consumer<Chunk> callback);

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(@NonNull Block block, @NonNull Consumer<Chunk> callback);

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>The {@link Consumer} will always be executed synchronously
     * on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @param gen whether to generate the chunk
     * @param callback Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    void getChunkAtAsync(@NonNull Block block, boolean gen, @NonNull Consumer<Chunk> callback);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates.
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the location at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(@NonNull Location loc);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the location at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(@NonNull Location loc);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the location at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(@NonNull Location loc, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the location at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(@NonNull Location loc, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(@NonNull Block block);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param block the block at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(@NonNull Block block);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(@NonNull Block block, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param block the block at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(@NonNull Block block, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(int x, int z);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(int x, int z);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(int x, int z, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the location at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Location loc);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the location at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronouslyUrgently(@NonNull Location loc);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param loc the location at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Location loc, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param loc the location at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronouslyUrgently(@NonNull Location loc, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Block block);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param block the block at which to load the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronouslyUrgently(@NonNull Block block);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param block the block at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Block block, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param block the block at which to load the chunk
     * @param gen whether to generate the chunk
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronouslyUrgently(@NonNull Block block, boolean gen);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsyncUrgently(int x, int z);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronouslyUrgently(int x, int z);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>non-async</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread.</p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @param gen whether to generate the chunk
     * @param urgent whether the requested load is urgent
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen, boolean urgent);

    /**
     * Requests a {@link Chunk} to be loaded at the given chunk coordinates
     *
     * <p>This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.</p>
     *
     * <p>You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.</p>
     *
     * <p>Adding a <i>sync</i> dependent operation to the returned future
     * is guaranteed to be run on the main server thread. <b>However, <i>non-async</i>
     * dependent operations may run on any thread.</b></p>
     *
     * @param x the chunk x coordinate
     * @param z the chunk z coordinate
     * @param gen whether to generate the chunk
     * @param urgent whether the requested load is urgent
     * @return a future that will complete when the chunk is loaded
     */
    @NonNull ReactionStage<Chunk> getChunkAtAsynchronously(int x, int z, boolean gen, boolean urgent);
    // Solar end

    /**
     * Checks if the specified {@link Chunk} is loaded
     *
     * @param chunk The chunk to check
     * @return true if the chunk is loaded, otherwise false
     */
    public boolean isChunkLoaded(Chunk chunk);

    /**
     * Gets an array of all loaded {@link Chunk}s
     *
     * @return Chunk[] containing all loaded chunks
     */
    public Chunk[] getLoadedChunks();

    /**
     * Loads the specified {@link Chunk}
     *
     * @param chunk The chunk to load
     */
    public void loadChunk(Chunk chunk);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded, otherwise false
     */
    public boolean isChunkLoaded(int x, int z);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded and
     * in use by one or more players
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded and in use by one or more players,
     *     otherwise false
     */
    public boolean isChunkInUse(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates
     * <p>
     * If the chunk does not exist, it will be generated.
     * <p>
     * This method is analogous to {@link #loadChunk(int, int, boolean)} where
     * generate is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     */
    public void loadChunk(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param generate Whether or not to generate a chunk if it doesn't
     *     already exist
     * @return true if the chunk has loaded successfully, otherwise false
     */
    public boolean loadChunk(int x, int z, boolean generate);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where safe and saveis true
     *
     * @param chunk the chunk to unload
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(Chunk chunk);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where safe and saveis true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(int x, int z);

    /**
     * Safely unloads and optionally saves the {@link Chunk} at the specified
     * coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where save is true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Whether or not to save the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(int x, int z, boolean save);

    /**
     * Unloads and optionally saves the {@link Chunk} at the specified
     * coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Controls whether the chunk is saved
     * @param safe Controls whether to unload the chunk when players are
     *     nearby
     * @return true if the chunk has unloaded successfully, otherwise false
     * @deprecated it is never safe to remove a chunk in use
     */
    @Deprecated
    public boolean unloadChunk(int x, int z, boolean save, boolean safe);

    /**
     * Safely queues the {@link Chunk} at the specified coordinates for
     * unloading
     * <p>
     * This method is analogous to {@link #unloadChunkRequest(int, int,
     * boolean)} where safe is true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true is the queue attempt was successful, otherwise false
     */
    public boolean unloadChunkRequest(int x, int z);

    /**
     * Queues the {@link Chunk} at the specified coordinates for unloading
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param safe Controls whether to queue the chunk when players are nearby
     * @return Whether the chunk was actually queued
     */
    public boolean unloadChunkRequest(int x, int z, boolean safe);

    /**
     * Regenerates the {@link Chunk} at the specified coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually regenerated
     */
    public boolean regenerateChunk(int x, int z);

    /**
     * Resends the {@link Chunk} to all clients
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually refreshed
     * 
     * @deprecated This method is not guaranteed to work suitably across all client implementations.
     */
    @Deprecated
    public boolean refreshChunk(int x, int z);

    /**
     * Drops an item at the specified {@link Location}
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    public Item dropItem(Location location, ItemStack item);

    /**
     * Drops an item at the specified {@link Location} with a random offset
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    public Item dropItemNaturally(Location location, ItemStack item);

    /**
     * Creates an {@link Arrow} entity at the given {@link Location}
     *
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @return Arrow entity spawned as a result of this method
     */
    public Arrow spawnArrow(Location location, Vector direction, float speed, float spread);

    /**
     * Creates an arrow entity of the given class at the given {@link Location}
     *
     * @param <T> type of arrow to spawn
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @param clazz the Entity class for the arrow
     * {@link org.bukkit.entity.SpectralArrow},{@link org.bukkit.entity.Arrow},{@link org.bukkit.entity.TippedArrow}
     * @return Arrow entity spawned as a result of this method
     */
    public <T extends Arrow> T spawnArrow(Location location, Vector direction, float speed, float spread, Class<T> clazz);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param location Location to spawn the tree
     * @param type Type of the tree to create
     * @return true if the tree was created successfully, otherwise false
     */
    public boolean generateTree(Location location, TreeType type);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param loc Location to spawn the tree
     * @param type Type of the tree to create
     * @param delegate A class to call for each block changed as a result of
     *     this method
     * @return true if the tree was created successfully, otherwise false
     * @deprecated rarely used API that was largely for implementation purposes
     */
    @Deprecated
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate);

    /**
     * Creates a entity at the given {@link Location}
     *
     * @param loc The location to spawn the entity
     * @param type The entity to spawn
     * @return Resulting Entity of this method, or null if it was unsuccessful
     */
    public Entity spawnEntity(Location loc, EntityType type);

    /**
     * Strikes lightning at the given {@link Location}
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    public LightningStrike strikeLightning(Location loc);

    /**
     * Strikes lightning at the given {@link Location} without doing damage
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    public LightningStrike strikeLightningEffect(Location loc);

    /**
     * Get a list of all entities in this World
     *
     * @return A List of all Entities currently residing in this world
     */
    public List<Entity> getEntities();

    /**
     * Get a list of all living entities in this World
     *
     * @return A List of all LivingEntities currently residing in this world
     */
    public List<LivingEntity> getLivingEntities();

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @Deprecated
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes);

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     * 
     * @param <T> an entity subclass
     * @param cls The class representing the type of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls);

    /**
     * Get a collection of all entities in this World matching any of the
     * given classes/interfaces
     *
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match one or more of the given classes/interfaces
     */
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes);

    // Paper start
    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double radius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double radius, Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double xzRadius, double yRadius, Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    public default Collection<LivingEntity> getNearbyLivingEntities(Location loc, double xRadius, double yRadius, double zRadius, Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double radius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @return the collection of players near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double radius, Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double xzRadius, double yRadius, Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    public default Collection<Player> getNearbyPlayers(Location loc, double xRadius, double yRadius, double zRadius, Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends T> clazz, Location loc, double radius) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends T> clazz, Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends T> clazz, Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(clazz, loc, xRadius, yRadius, zRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends T> clazz, Location loc, double radius, Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends T> clazz, Location loc, double xzRadius, double yRadius, Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, predicate);
    }

     /**
      * Gets all nearby entities of the specified type, within the specified radius (bounding box)
      * @param clazz Type to filter by
      * @param loc Center location
      * @param xRadius X Radius
      * @param yRadius Y Radius
      * @param zRadius Z Radius
      * @param predicate a predicate used to filter results
      * @param <T> the entity type
      * @return the collection of entities near location. This will always be a non-null collection.
      */
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(Class<? extends Entity> clazz, Location loc, double xRadius, double yRadius, double zRadius, Predicate<T> predicate) {
        if (clazz == null) {
            clazz = Entity.class;
        }
        List<T> nearby = new ArrayList<>();
        for (Entity bukkitEntity : getNearbyEntities(loc, xRadius, yRadius, zRadius)) {
            //noinspection unchecked
            if (clazz.isAssignableFrom(bukkitEntity.getClass()) && (predicate == null || predicate.test((T) bukkitEntity))) {
                //noinspection unchecked
                nearby.add((T) bukkitEntity);
            }
        }
        return nearby;
    }
    // Paper end

    /**
     * Get a list of all players in this World
     *
     * @return A list of all Players currently residing in this world
     */
    public List<Player> getPlayers();

    /**
     * Returns a list of entities within a bounding box centered around a Location.
     *
     * Some implementations may impose artificial restrictions on the size of the search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z);

    // Paper start - getEntity by UUID API
    /**
     * Gets an entity in this world by its UUID
     *
     * @param uuid the UUID of the entity
     * @return the entity with the given UUID, or null if it isn't found
     */
    public Entity getEntity(UUID uuid);
    // Paper end

    /**
     * Gets the unique name of this world
     *
     * @return Name of this world
     */
    public String getName();

    /**
     * Gets the Unique ID of this world
     *
     * @return Unique ID of this world.
     */
    public UUID getUID();

    /**
     * Gets the default spawn {@link Location} of this world
     *
     * @return The spawn location of this world
     */
    public Location getSpawnLocation();

    /**
     * Sets the spawn location of the world.
     * <br>
     * The location provided must be equal to this world.
     *
     * @param location The {@link Location} to set the spawn for this world at.
     * @return True if it was successfully set.
     */
    public boolean setSpawnLocation(Location location);

    /**
     * Sets the spawn location of the world
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return True if it was successfully set.
     */
    public boolean setSpawnLocation(int x, int y, int z);

    /**
     * Gets the relative in-game time of this world.
     * <p>
     * The relative time is analogous to hours * 1000
     *
     * @return The current relative time
     * @see #getFullTime() Returns an absolute time of this world
     */
    public long getTime();

    /**
     * Sets the relative in-game time on the server.
     * <p>
     * The relative time is analogous to hours * 1000
     * <p>
     * Note that setting the relative time below the current relative time
     * will actually move the clock forward a day. If you require to rewind
     * time, please see {@link #setFullTime(long)}
     *
     * @param time The new relative time to set the in-game time to (in
     *     hours*1000)
     * @see #setFullTime(long) Sets the absolute time of this world
     */
    public void setTime(long time);

    /**
     * Gets the full in-game time on this world
     *
     * @return The current absolute time
     * @see #getTime() Returns a relative time of this world
     */
    public long getFullTime();

    /**
     * Sets the in-game time on the server
     * <p>
     * Note that this sets the full time of the world, which may cause adverse
     * effects such as breaking redstone clocks and any scheduled events
     *
     * @param time The new absolute time to set this world to
     * @see #setTime(long) Sets the relative time of this world
     */
    public void setFullTime(long time);

    /**
     * Returns whether the world has an ongoing storm.
     *
     * @return Whether there is an ongoing storm
     */
    public boolean hasStorm();

    /**
     * Set whether there is a storm. A duration will be set for the new
     * current conditions.
     *
     * @param hasStorm Whether there is rain and snow
     */
    public void setStorm(boolean hasStorm);

    /**
     * Get the remaining time in ticks of the current conditions.
     *
     * @return Time in ticks
     */
    public int getWeatherDuration();

    /**
     * Set the remaining time in ticks of the current conditions.
     *
     * @param duration Time in ticks
     */
    public void setWeatherDuration(int duration);

    /**
     * Returns whether there is thunder.
     *
     * @return Whether there is thunder
     */
    public boolean isThundering();

    /**
     * Set whether it is thundering.
     *
     * @param thundering Whether it is thundering
     */
    public void setThundering(boolean thundering);

    /**
     * Get the thundering duration.
     *
     * @return Duration in ticks
     */
    public int getThunderDuration();

    /**
     * Set the thundering duration.
     *
     * @param duration Duration in ticks
     */
    public void setThunderDuration(int duration);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(Location loc, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(Location loc, float power, boolean setFire);

    // Paper start
    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(Entity source, Location loc, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy other blocks
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Entity source, Location loc, float power, boolean setFire) {
        return createExplosion(source, loc, power, setFire, true);
    }
    /**
     * Creates explosion at given location with given power, with the specified entity as the source.
     * Will set blocks on fire and destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Entity source, Location loc, float power) {
        return createExplosion(source, loc, power, true, true);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Entity source, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(source, source.getLocation(), power, setFire, breakBlocks);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Entity source, float power, boolean setFire) {
        return createExplosion(source, source.getLocation(), power, setFire, true);
    }

    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Entity source, float power) {
        return createExplosion(source, source.getLocation(), power, true, true);
    }

    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks);
    }
    // Paper end

    /**
     * Gets the {@link Environment} type of this world
     *
     * @return This worlds Environment type
     */
    public Environment getEnvironment();

    /**
     * Gets the Seed for this world.
     *
     * @return This worlds Seed
     */
    public long getSeed();

    /**
     * Gets the current PVP setting for this world.
     *
     * @return True if PVP is enabled
     */
    public boolean getPVP();

    /**
     * Sets the PVP setting for this world.
     *
     * @param pvp True/False whether PVP should be Enabled.
     */
    public void setPVP(boolean pvp);

    /**
     * Gets the chunk generator for this world
     *
     * @return ChunkGenerator associated with this world
     */
    public ChunkGenerator getGenerator();

    /**
     * Saves world to disk
     */
    public void save();

    /**
     * Gets a list of all applied {@link BlockPopulator}s for this World
     *
     * @return List containing any or none BlockPopulators
     */
    public List<BlockPopulator> getPopulators();

    /**
     * Spawn an entity of a specific class at the given {@link Location}
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException;

    /**
     * Spawn an entity of a specific class at the given {@link Location}, with
     * the supplied function run before the entity is added to the world.
     * <br>
     * Note that when the function is run, the entity will not be actually in
     * the world. Any operation involving such as teleporting the entity is undefined
     * until after this function returns.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param function the function to be run before the entity is spawned.
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     MaterialData} are null or {@link Material} of the {@link MaterialData} is not a block
     */
    public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param material The block {@link Material} type
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     Material} are null or {@link Material} is not a block
     * @deprecated Magic value
     */
    @Deprecated
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified blockId (converted to {@link Material})
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param blockId The id of the intended material
     * @param blockData The block data
     * @return The spawned FallingBlock instance
     * @throws IllegalArgumentException if location is null, or blockId is
     *     invalid
     * @see #spawnFallingBlock(org.bukkit.Location, org.bukkit.Material, byte)
     * @deprecated Magic value
     */
    @Deprecated
    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException;

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    public void playEffect(Location location, Effect effect, int data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    public void playEffect(Location location, Effect effect, int data, int radius);

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    public <T> void playEffect(Location location, Effect effect, T data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    public <T> void playEffect(Location location, Effect effect, T data, int radius);

    /**
     * Get empty chunk snapshot (equivalent to all air blocks), optionally
     * including valid biome data. Used for representing an ungenerated chunk,
     * or for fetching only biome data without loading a chunk.
     *
     * @param x - chunk x coordinate
     * @param z - chunk z coordinate
     * @param includeBiome - if true, snapshot includes per-coordinate biome
     *     type
     * @param includeBiomeTempRain - if true, snapshot includes per-coordinate
     *     raw biome temperature and rainfall
     * @return The empty snapshot.
     */
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain);

    /**
     * Sets the spawn flags for this.
     *
     * @param allowMonsters - if true, monsters are allowed to spawn in this
     *     world.
     * @param allowAnimals - if true, animals are allowed to spawn in this
     *     world.
     */
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);

    /**
     * Gets whether animals can spawn in this world.
     *
     * @return whether animals can spawn in this world.
     */
    public boolean getAllowAnimals();

    /**
     * Gets whether monsters can spawn in this world.
     *
     * @return whether monsters can spawn in this world.
     */
    public boolean getAllowMonsters();

    /**
     * Gets the biome for the given block coordinates.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Biome of the requested block
     */
    Biome getBiome(int x, int z);

    /**
     * Sets the biome for the given block coordinates
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @param bio new Biome type for this block
     */
    void setBiome(int x, int z, Biome bio);

    /**
     * Gets the temperature for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Temperature of the requested block
     */
    public double getTemperature(int x, int z);

    /**
     * Gets the humidity for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Humidity of the requested block
     */
    public double getHumidity(int x, int z);

    /**
     * Gets the maximum height of this world.
     * <p>
     * If the max height is 100, there are only blocks from y=0 to y=99.
     *
     * @return Maximum height of the world
     */
    public int getMaxHeight();

    /**
     * Gets the sea level for this world.
     * <p>
     * This is often half of {@link #getMaxHeight()}
     *
     * @return Sea level
     */
    public int getSeaLevel();

    /**
     * Gets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @return true if the world's spawn area will be kept loaded into memory.
     */
    public boolean getKeepSpawnInMemory();

    /**
     * Sets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @param keepLoaded if true then the world's spawn area will be kept
     *     loaded into memory.
     */
    public void setKeepSpawnInMemory(boolean keepLoaded);

    /**
     * Gets whether or not the world will automatically save
     *
     * @return true if the world will automatically save, otherwise false
     */
    public boolean isAutoSave();

    /**
     * Sets whether or not the world will automatically save
     *
     * @param value true if the world should automatically save, otherwise
     *     false
     */
    public void setAutoSave(boolean value);

    /**
     * Sets the Difficulty of the world.
     *
     * @param difficulty the new difficulty you want to set the world to
     */
    public void setDifficulty(Difficulty difficulty);

    /**
     * Gets the Difficulty of the world.
     *
     * @return The difficulty of the world.
     */
    public Difficulty getDifficulty();

    /**
     * Gets the folder of this world on disk.
     *
     * @return The folder of this world.
     */
    public File getWorldFolder();

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     */
    public WorldType getWorldType();

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     */
    public boolean canGenerateStructures();

    /**
     * Gets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @return The world's ticks per animal spawns value
     */
    public long getTicksPerAnimalSpawns();

    /**
     * Sets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @param ticksPerAnimalSpawns the ticks per animal spawns value you want
     *     to set the world to
     */
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns);

    /**
     * Gets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @return The world's ticks per monster spawns value
     */
    public long getTicksPerMonsterSpawns();

    /**
     * Sets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world on every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @param ticksPerMonsterSpawns the ticks per monster spawns value you
     *     want to set the world to
     */
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns);

    /**
     * Gets limit for number of monsters that can spawn in a chunk in this
     * world
     *
     * @return The monster spawn limit
     */
    int getMonsterSpawnLimit();

    /**
     * Sets the limit for number of monsters that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setMonsterSpawnLimit(int limit);

    /**
     * Gets the limit for number of animals that can spawn in a chunk in this
     * world
     *
     * @return The animal spawn limit
     */
    int getAnimalSpawnLimit();

    /**
     * Sets the limit for number of animals that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of water animals that can spawn in a chunk in
     * this world
     *
     * @return The water animal spawn limit
     */
    int getWaterAnimalSpawnLimit();

    /**
     * Sets the limit for number of water animals that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setWaterAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     *
     * @return The ambient spawn limit
     */
    int getAmbientSpawnLimit();

    /**
     * Sets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setAmbientSpawnLimit(int limit);

    /**
     * Play a Sound at the provided Location in the World
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Location location, Sound sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No
     * sound will be heard by the players if their clients do not have the
     * respective sound for the value passed.
     *
     * @param location the location to play the sound
     * @param sound the internal sound name to play
     * @param volume the volume of the sound
     * @param pitch the pitch of the sound
     */
    void playSound(Location location, String sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param category the category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No sound
     * will be heard by the players if their clients do not have the respective
     * sound for the value passed.
     *
     * @param location the location to play the sound
     * @param sound the internal sound name to play
     * @param category the category of the sound
     * @param volume the volume of the sound
     * @param pitch the pitch of the sound
     */
    void playSound(Location location, String sound, SoundCategory category, float volume, float pitch);

    /**
     * Get existing rules
     *
     * @return An array of rules
     */
    public String[] getGameRules();

    /**
     * Gets the current state of the specified rule
     * <p>
     * Will return null if rule passed is null
     *
     * @param rule Rule to look up value of
     * @return String value of rule
     */
    public String getGameRuleValue(String rule);

    /**
     * Set the specified gamerule to specified value.
     * <p>
     * The rule may attempt to validate the value passed, will return true if
     * value was set.
     * <p>
     * If rule is null, the function will return false.
     *
     * @param rule Rule to set
     * @param value Value to set rule to
     * @return True if rule was set
     */
    public boolean setGameRuleValue(String rule, String value);

    /**
     * Checks if string is a valid game rule
     *
     * @param rule Rule to check
     * @return True if rule exists
     */
    public boolean isGameRule(String rule);

    /**
     * Gets the world border for this world.
     *
     * @return The world border for this world.
     */
    public WorldBorder getWorldBorder();

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     */
    public void spawnParticle(Particle particle, Location location, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     */
    public void spawnParticle(Particle particle, double x, double y, double z, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(Particle particle, Location location, int count, T data);


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public default <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) { spawnParticle(particle, null, null, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }// Paper start - Expand Particle API
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public default <T> void spawnParticle(Particle particle, List<Player> receivers, Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) { spawnParticle(particle, receivers, source, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     * @param force allows the particle to be seen further away from the player
     *              and shows to players using any vanilla client particle settings
     */
    public <T> void spawnParticle(Particle particle, List<Player> receivers, Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force);
    // Paper end


    // Spigot start
    public class Spigot
    {

        /**
         * Plays an effect to all players within a default radius around a given
         * location.
         *
         * @param location the {@link Location} around which players must be to
         * see the effect
         * @param effect the {@link Effect}
         * @throws IllegalArgumentException if the location or effect is null.
         * It also throws when the effect requires a material or a material data
         * @deprecated Spigot specific API, use {@link Particle}.
         */
        @Deprecated
        public void playEffect(Location location, Effect effect)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        /**
         * Plays an effect to all players within a default radius around a given
         * location. The effect will use the provided material (and material
         * data if required). The particle's position on the client will be the
         * given location, adjusted on each axis by a normal distribution with
         * mean 0 and standard deviation given in the offset parameters, each
         * particle has independently calculated offsets. The effect will have
         * the given speed and particle count if the effect is a particle. Some
         * effect will create multiple particles.
         *
         * @param location the {@link Location} around which players must be to
         * see the effect
         * @param effect effect the {@link Effect}
         * @param id the item/block/data id for the effect
         * @param data the data value of the block/item for the effect
         * @param offsetX the amount to be randomly offset by in the X axis
         * @param offsetY the amount to be randomly offset by in the Y axis
         * @param offsetZ the amount to be randomly offset by in the Z axis
         * @param speed the speed of the particles
         * @param particleCount the number of particles
         * @param radius the radius around the location
         * @deprecated Spigot specific API, use {@link Particle}.
         */
        @Deprecated
        public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        /**
         * Strikes lightning at the given {@link Location} and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */        
        public LightningStrike strikeLightning(Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
        
        /**
         * Strikes lightning at the given {@link Location} without doing damage and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    Spigot spigot();
    // Spigot end

    /**
     * Represents various map environment types that a world may be
     */
    public enum Environment {

        /**
         * Represents the "normal"/"surface world" map
         */
        NORMAL(0),
        /**
         * Represents a nether based map ("hell")
         */
        NETHER(-1),
        /**
         * Represents the "end" map
         */
        THE_END(1);

        private final int id;
        private static final Map<Integer, Environment> lookup = new HashMap<Integer, Environment>();

        private Environment(int id) {
            this.id = id;
        }

        /**
         * Gets the dimension ID of this environment
         *
         * @return dimension ID
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }

        /**
         * Get an environment by ID
         *
         * @param id The ID of the environment
         * @return The environment
         * @deprecated Magic value
         */
        @Deprecated
        public static Environment getEnvironment(int id) {
            return lookup.get(id);
        }

        static {
            for (Environment env : values()) {
                lookup.put(env.getId(), env);
            }
        }
    }
}
