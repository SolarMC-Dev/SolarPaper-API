package co.aikar.timings.internal;

import co.aikar.timings.Timing;
import org.bukkit.plugin.Plugin;

public final class SafeTimings {

    private SafeTimings() {}

    /*
    =================
    Protected API: These are for internal use only in Bukkit/CraftBukkit
    These do not have isPrimaryThread() checks in the startTiming/stopTiming
    =================
    */

    static TimingHandler ofSafe(String name) {
        return ofSafe(null, name, null);
    }

    static Timing ofSafe(Plugin plugin, String name) {
        Timing pluginHandler = null;
        if (plugin != null) {
            pluginHandler = ofSafe(plugin.getName(), "Combined Total", TimingsManager.PLUGIN_GROUP_HANDLER);
        }
        return ofSafe(plugin != null ? plugin.getName() : "Minecraft - Invalid Plugin", name, pluginHandler);
    }

    static TimingHandler ofSafe(String name, Timing groupHandler) {
        return ofSafe(null, name, groupHandler);
    }

    public static TimingHandler ofSafe(String groupName, String name, Timing groupHandler) {
        return TimingsManager.getHandler(groupName, name, (InternalTiming) groupHandler);
    }

}
