package org.bukkit.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.function.UnaryOperator;

/**
 * An instance of the ItemFactory can be obtained with {@link
 * Server#getItemFactory()}.
 * <p>
 * The ItemFactory is solely responsible for creating item meta containers to
 * apply on item stacks.
 */
public interface ItemFactory {

    /**
     * This creates a new item meta for the material.
     *
     * @param material The material to consider as base for the meta
     * @return a new ItemMeta that could be applied to an item stack of the
     *     specified material
     */
    ItemMeta getItemMeta(final Material material);

    /**
     * This method checks the item meta to confirm that it is applicable (no
     * data lost if applied) to the specified ItemStack.
     * <p>
     * A {@link SkullMeta} would not be valid for a sword, but a normal {@link
     * ItemMeta} from an enchanted dirt block would.
     *
     * @param meta Meta to check
     * @param stack Item that meta will be applied to
     * @return true if the meta can be applied without losing data, false
     *     otherwise
     * @throws IllegalArgumentException if the meta was not created by this
     *     factory
     */
    boolean isApplicable(final ItemMeta meta, final ItemStack stack) throws IllegalArgumentException;

    /**
     * This method checks the item meta to confirm that it is applicable (no
     * data lost if applied) to the specified Material.
     * <p>
     * A {@link SkullMeta} would not be valid for a sword, but a normal {@link
     * ItemMeta} from an enchanted dirt block would.
     *
     * @param meta Meta to check
     * @param material Material that meta will be applied to
     * @return true if the meta can be applied without losing data, false
     *     otherwise
     * @throws IllegalArgumentException if the meta was not created by this
     *     factory
     */
    boolean isApplicable(final ItemMeta meta, final Material material) throws IllegalArgumentException;

    /**
     * This method is used to compare two item meta data objects.
     *
     * @param meta1 First meta to compare, and may be null to indicate no data
     * @param meta2 Second meta to compare, and may be null to indicate no
     *     data
     * @return false if one of the meta has data the other does not, otherwise
     *     true
     * @throws IllegalArgumentException if either meta was not created by this
     *     factory
     */
    boolean equals(final ItemMeta meta1, final ItemMeta meta2) throws IllegalArgumentException;

    /**
     * Returns an appropriate item meta for the specified stack.
     * <p>
     * The item meta returned will always be a valid meta for a given
     * ItemStack of the specified material. It may be a more or less specific
     * meta, and could also be the same meta or meta type as the parameter.
     * The item meta returned will also always be the most appropriate meta.
     * <p>
     * Example, if a {@link SkullMeta} is being applied to a book, this method
     * would return a {@link BookMeta} containing all information in the
     * specified meta that is applicable to an {@link ItemMeta}, the highest
     * common interface.
     *
     * @param meta the meta to convert
     * @param stack the stack to convert the meta for
     * @return An appropriate item meta for the specified item stack. No
     *     guarantees are made as to if a copy is returned. This will be null
     *     for a stack of air.
     * @throws IllegalArgumentException if the specified meta was not created
     *     by this factory
     */
    ItemMeta asMetaFor(final ItemMeta meta, final ItemStack stack) throws IllegalArgumentException;

    /**
     * Returns an appropriate item meta for the specified material.
     * <p>
     * The item meta returned will always be a valid meta for a given
     * ItemStack of the specified material. It may be a more or less specific
     * meta, and could also be the same meta or meta type as the parameter.
     * The item meta returned will also always be the most appropriate meta.
     * <p>
     * Example, if a {@link SkullMeta} is being applied to a book, this method
     * would return a {@link BookMeta} containing all information in the
     * specified meta that is applicable to an {@link ItemMeta}, the highest
     * common interface.
     *
     * @param meta the meta to convert
     * @param material the material to convert the meta for
     * @return An appropriate item meta for the specified item material. No
     *     guarantees are made as to if a copy is returned. This will be null for air.
     * @throws IllegalArgumentException if the specified meta was not created
     *     by this factory
     */
    ItemMeta asMetaFor(final ItemMeta meta, final Material material) throws IllegalArgumentException;

    /**
     * Returns the default color for all leather armor.
     *
     * @return the default color for leather armor
     */
    Color getDefaultLeatherColor();

    // Paper start
    /**
     * Minecart updates are converting simple item stacks into more complex NBT oriented Item Stacks.
     *
     * Use this method to to ensure any desired data conversions are processed.
     * The input itemstack will not be the same as the returned itemstack.
     *
     * @param item The item to process conversions on
     * @return A potentially Data Converted ItemStack
     */
    ItemStack ensureServerConversions(ItemStack item);

    /**
     * Gets the Display name as seen in the Client.
     * Currently the server only supports the English language. To override this,
     * You must replace the language file embedded in the server jar.
     *
     * @param item Item to return Display name of
     * @return Display name of Item
     */
    String getI18NDisplayName(ItemStack item);
    // Paper end

    // Solar start - adventure
    /**
     * Creates a hover event for the given item.
     *
     * @param itemStack item The item
     * @param op an operation
     * @return A hover event
     */
    HoverEvent<HoverEvent.ShowItem> asHoverEvent(final @NonNull ItemStack itemStack,
                                                 final @NonNull UnaryOperator<HoverEvent.ShowItem> op);

    /**
     * Get the formatted display name of the {@link ItemStack}.
     *
     * @param itemStack the {@link ItemStack}
     * @return display name of the {@link ItemStack}
     */
    Component displayName(@NonNull ItemStack itemStack);
    // Solar end

    // Solar start - ItemStack serialization API
    /**
     * Serializes the itemstack to NBT
     *
     * @param itemStack the itemstack
     * @return the itemstack data
     */
    byte @NonNull[] serializeAsBytes(@NonNull ItemStack itemStack);

    /**
     * Serializes the itemstack to NBT
     *
     * @param itemStack the itemstack
     * @param outputStream the output stream to which to write the itemstack data
     * @throws IOException if an I/O error occurs
     */
    void serializeAsBytes(@NonNull ItemStack itemStack, @NonNull OutputStream outputStream) throws IOException;

    /**
     * Serializes the itemstack to NBT
     *
     * @param itemStack the itemstack
     * @param outputChannel the output channel to which to write the itemstack data
     * @throws IOException if an I/O error occurs
     */
    void serializeAsBytes(@NonNull ItemStack itemStack, @NonNull WritableByteChannel outputChannel) throws IOException;

    /**
     * Deserializes an itemstack from NBT
     *
     * @param itemStackData the itemstack data
     * @return the itemstack
     */
    @NonNull ItemStack deserializeBytes(byte @NonNull[] itemStackData);

    /**
     * Deserializes an itemstack from NBT
     *
     * @param itemStackData the stream from which to read the itemstack data
     * @return the itemstack
     * @throws IOException if an I/O error occurs
     */
    @NonNull ItemStack deserializeBytes(@NonNull InputStream itemStackData) throws IOException;

    /**
     * Deserializes an itemstack from NBT
     *
     * @param itemStackData the channel from which to read the itemstack data
     * @return the itemstack
     * @throws IOException if an I/O error occurs
     */
    @NonNull ItemStack deserializeBytes(@NonNull ReadableByteChannel itemStackData) throws IOException;
    // Solar end
}
