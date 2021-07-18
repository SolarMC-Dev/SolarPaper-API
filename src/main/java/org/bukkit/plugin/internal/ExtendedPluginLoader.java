package org.bukkit.plugin.internal;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public interface ExtendedPluginLoader extends PluginLoader {

    Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
            final Listener listener, final Plugin plugin, EventExecutorFactory eventExecutorFactory);

    <J extends JavaPlugin> J initPlugin(PluginData<J> pluginData) throws InvalidPluginException;

}
