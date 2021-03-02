package org.bukkit.plugin;

import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;

import java.io.File;
import java.util.List;
import java.util.Map;

@InternalApiDoNotUse
public final class BridgePluginManager extends SimplePluginManager implements PluginHolder {

    private static final String NOT_SUPPORTED = "Unfortunately, this operation from PluginManager is not supported " +
            "by the modified SolarMC plugin loader. - A248";

    public BridgePluginManager(Server instance, SimpleCommandMap commandMap) {
        super(instance, commandMap);
    }

    private UnsupportedOperationException notSupported() {
        return new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
        throw notSupported();
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        throw notSupported();
    }

    @Override
    public Plugin[] loadPlugins(File directory) {
        throw notSupported();
    }

    @Override
    public void addPlugin(Plugin plugin) {
        internalAddPlugin(plugin);
    }

    @Override
    public <T> List<Plugin> sortPlugins(Map<T, PluginDescriptionFile> plugins, ObjectToPlugin<T> toPlugin) {
        return sortPluginDependencies(plugins, toPlugin);
    }

}
