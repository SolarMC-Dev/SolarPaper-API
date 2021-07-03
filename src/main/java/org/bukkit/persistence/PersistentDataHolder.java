package org.bukkit.persistence;

import org.checkerframework.checker.nullness.qual.NonNull;

// Solar - whole class
/**
 * The {@link PersistentDataHolder} interface defines an object that can store
 * custom persistent meta data on it.
 */
public interface PersistentDataHolder {

    /**
     * Returns a custom tag container capable of storing tags on the object.
     *
     * Note that the tags stored on this container are all stored under their
     * own custom namespace therefore modifying default tags using this
     * {@link PersistentDataHolder} is impossible.
     *
     * @return the persistent metadata container
     */
    @NonNull
    PersistentDataContainer getPersistentDataContainer();

}
