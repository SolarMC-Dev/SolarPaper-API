package org.bukkit.plugin;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Alternative plugin loading interface, implementations of which are found
 * through the service loader. <br>
 * <br>
 * The equivalent loading, enabling, and disabling methods for instances of this interface are
 * {@link #onLaunch(Plugin, Path)}, {@link #onEnable}, {@link #close()}. <br>
 * <br>
 * Implementations are provided a {@link Plugin} for use with API methods expecting plugin objects.
 * The behavior of the provided {@code Plugin}'s various loading, enabling, and disabling methods
 * is explicitly unspecified.
 *
 */
// Solar - whole class
public interface LaunchablePlugin extends AutoCloseable {

    /**
     * Launches using the specified plugin and data folder. <br>
     * <br>
     * It is recommended, but not an absolute requirement, to use the provided
     * data folder rather than {@link Plugin#getDataFolder()}. This is because
     * {@code Plugin} instances may be shared across multiple {@code LaunchablePlugin}s.
     *
     * @param plugin the plugin
     * @param dataFolder the data folder
     */
    void onLaunch(Plugin plugin, Path dataFolder);

    /**
     * Enables the plugin
     *
     */
    void onEnable();

    /**
     * Gets a description for this plugin
     *
     * @return a description if the plugin wishes to provide one
     */
    default Optional<PluginDescriptionFile> getDescription() {
        return Optional.empty();
    }

    /**
     * Closes this plugin
     *
     */
    @Override
    void close();

}
