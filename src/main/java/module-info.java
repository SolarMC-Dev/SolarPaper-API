module org.bukkit {

    // JDK dependencies
    requires transitive java.logging; // Plugin#getLogger
    requires java.management; // Timings retrieves some system information through platform mbeans
    requires transitive java.desktop; // org.bukkit.map.MapPalette needs it

    // Library dependencies
    requires transitive org.slf4j;
    requires transitive gg.solarmc.loader;
    requires transitive org.spigotmc.bungee.api.chat;
    requires transitive org.checkerframework.checker.qual;

    requires org.yaml.snakeyaml;
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires org.apache.commons.lang3;
    requires com.google.common;
    requires com.google.gson;
    requires it.unimi.dsi.fastutil;

    // Export to everyone

    // co.aikar
    exports co.aikar.timings;

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
    exports org.bukkit.configuration;
    exports org.bukkit.configuration.file;
    exports org.bukkit.configuration.serialization;
    exports org.bukkit.conversations;
    exports org.bukkit.enchantments;
    exports org.bukkit.entity;
    exports org.bukkit.entity.minecart;
    exports org.bukkit.event;
    exports org.bukkit.event.block;
    exports org.bukkit.event.command;
    exports org.bukkit.event.enchantment;
    exports org.bukkit.event.entity;
    exports org.bukkit.event.hanging;
    exports org.bukkit.event.inventory;
    exports org.bukkit.event.player;
    exports org.bukkit.event.server;
    exports org.bukkit.event.vehicle;
    exports org.bukkit.event.weather;
    exports org.bukkit.event.world;
    exports org.bukkit.generator;
    exports org.bukkit.help;
    exports org.bukkit.inventory;
    exports org.bukkit.inventory.meta;
    exports org.bukkit.map;
    exports org.bukkit.material;
    exports org.bukkit.material.types;
    exports org.bukkit.metadata;
    exports org.bukkit.permissions;
    exports org.bukkit.plugin;
    exports org.bukkit.plugin.java;
    exports org.bukkit.plugin.messaging;
    exports org.bukkit.potion;
    exports org.bukkit.projectiles;
    exports org.bukkit.scheduler;
    exports org.bukkit.scoreboard;
    exports org.bukkit.util;
    exports org.bukkit.util.io;
    exports org.bukkit.util.noise;
    exports org.bukkit.util.permissions;

    // org.spigotmc
    exports org.spigotmc.event.entity;
    exports org.spigotmc.event.player;

    // Qualified exports to SolarPaper-Server and SolarPaper-Assistant
    exports co.aikar.timings.internal to org.bukkit.craftbukkit;
    exports co.aikar.util to org.bukkit.craftbukkit;
    exports com.destroystokyo.paper.event.executor to org.bukkit.craftbukkit, gg.solarmc.serverassistant;
    exports com.destroystokyo.paper.utils to org.bukkit.craftbukkit;
    exports org.bukkit.plugin.internal to org.bukkit.craftbukkit;

    /*
    Fully encapsulated:
    com.destroystokyo.paper.utils
    org.bukkit.command.defaults
     */
}