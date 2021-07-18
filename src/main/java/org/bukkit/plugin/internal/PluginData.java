package org.bukkit.plugin.internal;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;

public record PluginData<J extends JavaPlugin>(Class<J> pluginClass,
                                               PluginLoader loader, Server server, PluginDescriptionFile description,
                                               Path dataFolder, Path file,
                                               java.util.logging.Logger julLogger) {

    /**
     * Assistant factory method to help with generics as well as {@code Path} versus {@code File}
     *
     * @param pluginClass see class
     * @param loader see class
     * @param server see class
     * @param description see class
     * @param dataFolder see class
     * @param file see class
     * @param julLogger see class
     * @return the plugin data
     */
    public static PluginData<?> create(Class<? extends JavaPlugin> pluginClass,
                                       PluginLoader loader, Server server, PluginDescriptionFile description,
                                       File dataFolder, File file,
                                       java.util.logging.Logger julLogger) {
        return new PluginData<>(pluginClass, loader, server, description,
                dataFolder.toPath(), file.toPath(), julLogger);
    }

}
