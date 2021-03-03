package org.bukkit.plugin.internal;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public interface EventExecutorFactory {

    EventExecutor create(Plugin plugin, Listener listener, Method method, Class<? extends Event> eventClass);

}
