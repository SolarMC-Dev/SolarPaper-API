package org.bukkit.plugin.java;

import org.bukkit.Server;
import org.bukkit.plugin.InternalApiDoNotUse;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.nio.file.Path;

@InternalApiDoNotUse
public final class JavaPluginPackageAccess {

    private JavaPluginPackageAccess() {}

    public static void initPlugin(JavaPlugin plugin, PluginLoader loader, Server server, PluginDescriptionFile description,
                           Path dataFolder, Path file, ClassLoader classLoader) {
        plugin.init(loader, server, description, dataFolder.toFile(), file.toFile(), classLoader);
    }

    public static void setJulLogger(JavaPlugin plugin, java.util.logging.Logger julLogger) {
        plugin.logger = julLogger;
    }

}
