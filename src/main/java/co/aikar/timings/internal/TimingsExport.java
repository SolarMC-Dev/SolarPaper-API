/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings.internal;

import co.aikar.timings.TimingsReportListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.omnibus.util.ThisClass;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import static co.aikar.timings.internal.TimingsManager.HISTORY;
import static co.aikar.util.JSONUtil.appendObjectData;
import static co.aikar.util.JSONUtil.createObject;
import static co.aikar.util.JSONUtil.pair;
import static co.aikar.util.JSONUtil.toArray;
import static co.aikar.util.JSONUtil.toArrayMapper;
import static co.aikar.util.JSONUtil.toObjectMapper;

@SuppressWarnings({"rawtypes", "SuppressionAnnotation"})
public class TimingsExport extends Thread {

    private final TimingsReportListener listeners;
    private final Map out;
    private final TimingHistory[] history;
    private static long lastReport = 0;
    public final static List<CommandSender> requestingReport = Lists.newArrayList();

    private static final Logger LOGGER = LoggerFactory.getLogger(ThisClass.get());

    private TimingsExport(TimingsReportListener listeners, Map out, TimingHistory[] history) {
        super("Timings paste thread");
        this.listeners = listeners;
        this.out = out;
        this.history = history;
    }

    /**
     * Checks if any pending reports are being requested, and builds one if needed.
     */
    static void reportTimings() {
        if (requestingReport.isEmpty()) {
            return;
        }
        TimingsReportListener listeners = new TimingsReportListener(requestingReport);
        listeners.addConsoleIfNeeded();

        requestingReport.clear();
        long now = System.currentTimeMillis();
        final long lastReportDiff = now - lastReport;
        if (lastReportDiff < 60000) {
            listeners.sendMessage(ChatColor.RED + "Please wait at least 1 minute in between Timings reports. (" + (int)((60000 - lastReportDiff) / 1000) + " seconds)");
            listeners.done();
            return;
        }
        final long lastStartDiff = now - TimingsManager.timingStart;
        if (lastStartDiff < 180000) {
            listeners.sendMessage(ChatColor.RED + "Please wait at least 3 minutes before generating a Timings report. Unlike Timings v1, v2 benefits from longer timings and is not as useful with short timings. (" + (int)((180000 - lastStartDiff) / 1000) + " seconds)");
            listeners.done();
            return;
        }
        listeners.sendMessage(ChatColor.GREEN + "Preparing Timings Report...");
        lastReport = now;
        Map parent = createObject(
            // Get some basic system details about the server
            pair("version", Bukkit.getVersion()),
            pair("maxplayers", Bukkit.getMaxPlayers()),
            pair("start", TimingsManager.timingStart / 1000),
            pair("end", System.currentTimeMillis() / 1000),
            pair("sampletime", (System.currentTimeMillis() - TimingsManager.timingStart) / 1000)
        );
        if (!TimingsManager.privacy) {
            appendObjectData(parent,
                pair("server", Bukkit.getServerName()),
                pair("motd", Bukkit.getServer().getMotd()),
                pair("online-mode", Bukkit.getServer().getOnlineMode()),
                pair("icon", Bukkit.getServer().getServerIcon().getData())
            );
        }

        final Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        parent.put("system", createObject(
                pair("timingcost", getCost()),
                pair("name", System.getProperty("os.name")),
                pair("version", System.getProperty("os.version")),
                pair("jvmversion", System.getProperty("java.version")),
                pair("arch", System.getProperty("os.arch")),
                pair("maxmem", runtime.maxMemory()),
                pair("cpu", runtime.availableProcessors()),
                pair("runtime", ManagementFactory.getRuntimeMXBean().getUptime()),
                pair("flags", StringUtils.join(runtimeBean.getInputArguments(), " ")),
                pair("gc", toObjectMapper(ManagementFactory.getGarbageCollectorMXBeans(), input -> pair(input.getName(), toArray(input.getCollectionCount(), input.getCollectionTime()))))
            )
        );

        Set<Material> tileEntityTypeSet = Sets.newHashSet();
        Set<EntityType> entityTypeSet = Sets.newHashSet();

        int size = HISTORY.size();
        TimingHistory[] history = new TimingHistory[size + 1];
        int i = 0;
        for (TimingHistory timingHistory : HISTORY) {
            tileEntityTypeSet.addAll(timingHistory.tileEntityTypeSet);
            entityTypeSet.addAll(timingHistory.entityTypeSet);
            history[i++] = timingHistory;
        }

        history[i] = new TimingHistory(); // Current snapshot
        tileEntityTypeSet.addAll(history[i].tileEntityTypeSet);
        entityTypeSet.addAll(history[i].entityTypeSet);


        Map handlers = createObject();
        Map groupData;
        synchronized (TimingIdentifier.GROUP_MAP) {
            for (TimingIdentifier.TimingGroup group : TimingIdentifier.GROUP_MAP.values()) {
                synchronized (group.handlers) {
                    for (TimingHandler id : group.handlers) {
                        if (!id.isTimed() && !id.isSpecial()) {
                            continue;
                        }

                        String name = id.identifier.name;
                        if (name.startsWith("##")) {
                            name = name.substring(3);
                        }
                        handlers.put(id.id, toArray(
                            group.id,
                            name
                        ));
                    }
                }
            }

            groupData = toObjectMapper(TimingIdentifier.GROUP_MAP.values(), group -> pair(group.id, group.name));
        }
        parent.put("idmap", createObject(
            pair("groups", groupData),
            pair("handlers", handlers),
            pair("worlds", toObjectMapper(TimingHistory.worldMap.entrySet(), input -> pair(input.getValue(), input.getKey()))),
            pair("tileentity",
                toObjectMapper(tileEntityTypeSet, input -> pair(input.getId(), input.name()))),
            pair("entity",
                toObjectMapper(entityTypeSet, input -> pair(input.getTypeId(), input.name())))
        ));

        // Information about loaded plugins

        parent.put("plugins", toObjectMapper(Bukkit.getPluginManager().getPlugins(),
                plugin -> pair(plugin.getName(), createObject(
                    pair("version", plugin.getDescription().getVersion()),
                    pair("description", String.valueOf(plugin.getDescription().getDescription()).trim()),
                    pair("website", plugin.getDescription().getWebsite()),
                    pair("authors", StringUtils.join(plugin.getDescription().getAuthors(), ", "))
                ))));



        // Information on the users Config

        parent.put("config", createObject(
            pair("spigot", mapAsJSON(Bukkit.spigot().getSpigotConfig(), null)),
            pair("bukkit", mapAsJSON(Bukkit.spigot().getBukkitConfig(), null)),
            pair("paper", mapAsJSON(Bukkit.spigot().getPaperConfig(), null))
        ));

        new TimingsExport(listeners, parent, history).start();
    }

    public static long getCost() {
        // Benchmark the users System.nanotime() for cost basis
        int passes = 100;
        TimingHandler SAMPLER1 = SafeTimings.ofSafe("Timings Sampler 1");
        TimingHandler SAMPLER2 = SafeTimings.ofSafe("Timings Sampler 2");
        TimingHandler SAMPLER3 = SafeTimings.ofSafe("Timings Sampler 3");
        TimingHandler SAMPLER4 = SafeTimings.ofSafe("Timings Sampler 4");
        TimingHandler SAMPLER5 = SafeTimings.ofSafe("Timings Sampler 5");
        TimingHandler SAMPLER6 = SafeTimings.ofSafe("Timings Sampler 6");

        long start = System.nanoTime();
        for (int i = 0; i < passes; i++) {
            SAMPLER1.startTiming();
            SAMPLER2.startTiming();
            SAMPLER3.startTiming();
            SAMPLER3.stopTiming();
            SAMPLER4.startTiming();
            SAMPLER5.startTiming();
            SAMPLER6.startTiming();
            SAMPLER6.stopTiming();
            SAMPLER5.stopTiming();
            SAMPLER4.stopTiming();
            SAMPLER2.stopTiming();
            SAMPLER1.stopTiming();
        }
        long timingsCost = (System.nanoTime() - start) / passes / 6;
        SAMPLER1.reset(true);
        SAMPLER2.reset(true);
        SAMPLER3.reset(true);
        SAMPLER4.reset(true);
        SAMPLER5.reset(true);
        SAMPLER6.reset(true);
        return timingsCost;
    }

    // Solar start - use Gson instead of json-simple
    private static Map<String, Object> mapAsJSON(ConfigurationSection config, String parentKey) {

        Map<String, Object> object = new LinkedHashMap<>();
    // Solar end
        for (String key : config.getKeys(false)) {
            String fullKey = (parentKey != null ? parentKey + "." + key : key);
            if (fullKey.equals("database") || fullKey.equals("settings.bungeecord-addresses") || TimingsManager.hiddenConfigs.contains(fullKey)) {
                continue;
            }
            final Object val = config.get(key);

            object.put(key, valAsJSON(val, fullKey));
        }
        return object;
    }

    private static Object valAsJSON(Object val, final String parentKey) {
        if (!(val instanceof MemorySection)) {
            if (val instanceof List) {
                Iterable<Object> v = (Iterable<Object>) val;
                return toArrayMapper(v, input -> valAsJSON(input, parentKey));
            } else {
                return val.toString();
            }
        } else {
            return mapAsJSON((ConfigurationSection) val, parentKey);
        }
    }

    @Override
    public void run() {
        out.put("data", toArrayMapper(history, TimingHistory::export));


        String response = null;
        String timingsURL = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://timings.aikar.co/post").openConnection();
            con.setDoOutput(true);
            String hostName = "BrokenHost";
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch(Exception ignored) {}
            con.setRequestProperty("User-Agent", "Paper/" + Bukkit.getServerName() + "/" + hostName);
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);

            // Solar - switch to Gson and use try-with-resources
            try (OutputStream request = new GZIPOutputStream(con.getOutputStream()) {{
                this.def.setLevel(7);
            }}; Writer requestWriter = new BufferedWriter(new OutputStreamWriter(request, StandardCharsets.UTF_8))) {

            new Gson().toJson(out, new TypeToken<Map<String, Object>>(){}.getType(), requestWriter);
            }
            // Solar end

            response = getResponse(con);

            if (con.getResponseCode() != 302) {
                listeners.sendMessage(
                    ChatColor.RED + "Upload Error: " + con.getResponseCode() + ": " + con.getResponseMessage());
                listeners.sendMessage(ChatColor.RED + "Check your logs for more information");
                if (response != null) {
                    LOGGER.error(response);
                }
                return;
            }

            timingsURL = con.getHeaderField("Location");
            listeners.sendMessage(ChatColor.GREEN + "View Timings Report: " + timingsURL);

            if (response != null && !response.isEmpty()) {
                LOGGER.info("Timing Response: {}", response);
            }
        } catch (IOException ex) {
            listeners.sendMessage(ChatColor.RED + "Error uploading timings, check your logs for more information");
            if (response != null) {
                LOGGER.error(response);
            }
            LOGGER.error("Could not paste timings", ex);
        } finally {
            this.listeners.done(timingsURL);
        }
    }

    private String getResponse(HttpURLConnection con) throws IOException {
        try (InputStream is = con.getInputStream()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            is.transferTo(bos);
            return bos.toString();

        } catch (IOException ex) {
            listeners.sendMessage(ChatColor.RED + "Error uploading timings, check your logs for more information");
            LOGGER.warn(con.getResponseMessage(), ex);
            return null;
        }
    }
}
