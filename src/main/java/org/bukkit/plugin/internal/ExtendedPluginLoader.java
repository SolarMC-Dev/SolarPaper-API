package org.bukkit.plugin.internal;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface ExtendedPluginLoader extends PluginLoader {

    Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
            final Listener listener, final Plugin plugin, EventExecutorFactory eventExecutorFactory);

    void initPlugin(JavaPlugin plugin, PluginLoader loader, Server server, PluginDescriptionFile description,
                    Path dataFolder, Path file);

    void setJulLogger(JavaPlugin plugin, java.util.logging.Logger julLogger);

}
