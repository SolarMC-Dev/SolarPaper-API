package org.bukkit;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Nameable {

    // Solar start - adventure
    /**
     * Gets the custom name. <br>
     * <br>
     * This value has no effect on players, they will always use their real name.
     *
     * @return the custom name, or null if there is none
     */
    @Nullable
    Component customName();

    /**
     * Sets the custom name. <br>
     * <br>
     * This name will be used in death messages and can be sent to the client as a nameplate over the mob. <br>
     * <br>
     * This value has no effect on players, they will always use their real name.
     *
     * @param customName the custom name, or null to clear the name set
     */
    void customName(@Nullable Component customName);

    /**
     * Gets the custom name on a mob or block. If there is no name this method
     * will return null.
     * <p>
     * This value has no effect on players, they will always use their real
     * name.
     *
     * @return name of the mob/block or null
     * @deprecated Use the adventure {@link #customName()} instead
     */
    @Deprecated
    String getCustomName();

    /**
     * Sets a custom name on a mob or block. This name will be used in death
     * messages and can be sent to the client as a nameplate over the mob.
     * <p>
     * Setting the name to null or an empty string will clear it.
     * <p>
     * This value has no effect on players, they will always use their real
     * name.
     *
     * @param name the name to set
     * @deprecated Use the adventure {@link #customName(Component)} instead
     */
    @Deprecated
    void setCustomName(String name);
    // Solar end
}
