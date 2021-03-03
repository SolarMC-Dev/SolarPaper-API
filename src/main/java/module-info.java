module org.bukkit {

    // JDK dependencies
    requires java.logging;

    // Library dependencies
    // TODO: add all dependencies here

    requires transitive org.slf4j;
    requires transitive gg.solarmc.loader;
    requires transitive bungeecord.chat; // TODO: add module name for bungee-chat
    requires commons.lang; // TODO: migrate to commons 3
    requires org.yaml.snakeyaml;
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires guava; // TODO: migrate to newer guava

    // TODO: export literally everything else

    // Export to everyone

    // co.aikar
    exports co.aikar.timings; // TODO fix split package with SolarPaper-Server's co.aikar.timings

    // com.destroystokyo.paper
    exports com.destroystokyo.paper;
    exports com.destroystokyo.paper.entity;
    exports com.destroystokyo.paper.event.block;
    exports com.destroystokyo.paper.event.entity;
    exports com.destroystokyo.paper.event.player;
    exports com.destroystokyo.paper.event.profile;
    exports com.destroystokyo.paper.event.server;
    exports com.destroystokyo.paper.exception;
    exports com.destroystokyo.paper.inventory.meta;
    exports com.destroystokyo.paper.loottable;
    exports com.destroystokyo.paper.network;
    exports com.destroystokyo.paper.profile;

    // org.bukkit
    exports org.bukkit;
    exports org.bukkit.advancement;
    exports org.bukkit.attribute;
    exports org.bukkit.block;
    exports org.bukkit.block.banner;
    exports org.bukkit.block.structure;
    exports org.bukkit.boss;
    exports org.bukkit.command;
    // Add more here...
    exports org.bukkit.plugin;
    exports org.bukkit.plugin.java;
    exports org.bukkit.plugin.messaging;

    exports org.spigotmc.event.entity;
    exports org.spigotmc.event.player;

    // Qualified exports to SolarPaper-Server
    exports com.destroystokyo.paper.event.executor to org.bukkit.craftbukkit;
    exports com.destroystokyo.paper.utils to org.bukkit.craftbukkit;
    exports org.bukkit.plugin.internal to org.bukkit.craftbukkit;

    /*
    Fully encapsulated:
    co.aikar.util
    com.destroystokyo.paper.utils
    org.bukkit.command.defaults
     */
}