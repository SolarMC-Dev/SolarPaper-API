package com.destroystokyo.paper.event.executor;

import java.lang.invoke.MethodHandles;

/**
 * Class definer which creates a class in the same module as the listener class,
 * relying on the listener to {@code open} its package to {@code org.bukkit}
 *
 */
public final class ModularClassDefiner implements ClassDefiner {

    @Override
    public Class<?> defineClass(Class<?> listenerClass, String name, byte[] data) {
        Module listenerModule = listenerClass.getModule();
        Module ourModule = getClass().getModule();
        ourModule.addReads(listenerModule);
        try {
            return MethodHandles.privateLookupIn(listenerClass, MethodHandles.lookup()).defineClass(data);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to access listener module. If the listener class is not loaded through " +
                    "the plugin module path, it must open its enclosing package to the org.bukkit module",
                    ex);
        }
    }
}
