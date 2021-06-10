package org.bukkit.plugin.internal;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public final class AccessibleJavaPluginLoader extends JavaPluginLoader implements ExtendedPluginLoader {

    public AccessibleJavaPluginLoader(Server instance) {
        super(instance);
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
            final Listener listener, final Plugin plugin, EventExecutorFactory eventExecutorFactory) {
        return super.createRegisteredListeners(listener, plugin, eventExecutorFactory);
    }

    @Override
    public void initPlugin(JavaPlugin plugin, PluginLoader loader, Server server, PluginDescriptionFile description,
                                  Path dataFolder, Path file) {
        super.initPlugin(plugin, loader, server, description, dataFolder, file);
    }

    @Override
    public void setJulLogger(JavaPlugin plugin, java.util.logging.Logger julLogger) {
        super.setJulLogger(plugin, julLogger);
    }

}
