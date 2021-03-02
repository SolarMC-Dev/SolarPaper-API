package org.bukkit.plugin;

import java.util.List;
import java.util.Map;

@InternalApiDoNotUse
public interface PluginHolder {

    void addPlugin(Plugin plugin);

    <T> List<Plugin> sortPlugins(Map<T, PluginDescriptionFile> plugins, ObjectToPlugin<T> toPlugin);

    interface ObjectToPlugin<T> {

        Plugin toPlugin(T object) throws InvalidPluginException;
    }

    Plugin getPlugin(String name);

    boolean isPluginEnabled(String name);

    boolean isPluginEnabled(Plugin plugin);

}
