package org.bukkit.plugin.java;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JavaPluginTest {

    private final Server server;
    private final PluginManager pluginManager;

    public JavaPluginTest(@Mock Server server, @Mock PluginManager pluginManager) {
        this.server = server;
        this.pluginManager = pluginManager;
    }

    @BeforeEach
    public void setServer() {
        Bukkit.setServer(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
    }

    @AfterEach
    public void resetServer() {
        Bukkit.resetServer();
    }

    private static final class ExistentPlugin extends JavaPlugin {
        ExistentPlugin(Server server, PluginLoader loader, Path dataFolder, Path file) {
            super(loader, server,
                    new PluginDescriptionFile("ExistentPlugin", "1.0", ExistentPlugin.class.getName()),
                    dataFolder, file);
        }
    }

    private static final class NonexistentPlugin extends JavaPlugin { }

    @Test
    public void getPlugin(@TempDir Path dataFolder) {
        ExistentPlugin existentPlugin = new ExistentPlugin(
                server, mock(PluginLoader.class), dataFolder, dataFolder.resolve("plugin.jar"));
        when(pluginManager.getPlugins()).thenReturn(new Plugin[] {existentPlugin});
        assertEquals(existentPlugin, JavaPlugin.getPlugin(ExistentPlugin.class));
        assertThrows(IllegalArgumentException.class, () -> JavaPlugin.getPlugin(NonexistentPlugin.class));
        assertThrows(IllegalArgumentException.class, () -> JavaPlugin.getPlugin(JavaPlugin.class));
    }

    @Test
    public void getProvidingPlugin() {
        when(pluginManager.getPlugins()).thenReturn(new Plugin[] {});
        assertThrows(IllegalArgumentException.class, () -> JavaPlugin.getProvidingPlugin(JavaPlugin.class));
        assertThrows(IllegalArgumentException.class, () -> JavaPlugin.getProvidingPlugin(NonexistentPlugin.class));
        assertThrows(IllegalArgumentException.class, () -> JavaPlugin.getProvidingPlugin(String.class));
    }
}
