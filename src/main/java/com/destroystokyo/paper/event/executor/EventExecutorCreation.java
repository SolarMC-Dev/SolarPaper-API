package com.destroystokyo.paper.event.executor;

import com.google.common.base.Preconditions;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.omnibus.util.ThisClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class EventExecutorCreation {

    private final Listener listener;
    private final Method method;
    private final Class<? extends Event> eventClass;
    private final ClassDefiner classDefiner;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThisClass.get());

    public EventExecutorCreation(Listener listener, Method method, Class<? extends Event> eventClass, ClassDefiner classDefiner) {
        this.listener = Objects.requireNonNull(listener, "listener");
        this.method = Objects.requireNonNull(method, "method");
        this.eventClass = Objects.requireNonNull(eventClass, "eventClass");
        this.classDefiner = Objects.requireNonNull(classDefiner, "classDefiner");

        int paramCount = method.getParameterCount();
        Preconditions.checkArgument(paramCount != 0, "Incorrect number of arguments %s", paramCount);
        Class<?> paramType = method.getParameterTypes()[0];
        Preconditions.checkArgument(paramType == eventClass, "First parameter %s doesn't match event class %s", paramType, eventClass);
    }

    private void logDeop(String methodType) {
        LOGGER.trace("Creating event executor for {} method {} in {}. De-optimization may ensue.",
                methodType, method.getName(), method.getDeclaringClass());
    }

    public EventExecutor create() {
        if (Modifier.isStatic(method.getModifiers())) {
            logDeop("static");
            return new StaticMethodHandleEventExecutor(eventClass, method);
        }
        if (!method.canAccess(listener)) {
            logDeop("inaccessible");
            return new MethodHandleEventExecutor(eventClass, method);
        }
        String name = ASMEventExecutorGenerator.generateName();
        byte[] classData = ASMEventExecutorGenerator.generateEventExecutor(method, name);
        Class<? extends EventExecutor> executorClass = classDefiner.defineClass(method.getDeclaringClass(), name, classData)
                .asSubclass(EventExecutor.class);

        EventExecutor asmExecutor;
        try {
            asmExecutor = executorClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to initialize generated event executor", e);
        }
        // Define a wrapper to conform to bukkit stupidity (passing in events that don't match and wrapper exception)
        return new EventExecutorWrapper(eventClass, asmExecutor);
    }

    private static final class EventExecutorWrapper implements EventExecutor {

        private final Class<?> eventClass;
        private final EventExecutor delegate;

        private EventExecutorWrapper(Class<?> eventClass, EventExecutor delegate) {
            this.eventClass = eventClass;
            this.delegate = delegate;
        }

        @Override
        public void execute(Listener listener, Event event) throws EventException {
            if (!eventClass.isInstance(event)) {
                return;
            }
            try {
                delegate.execute(listener, event);
            } catch (Exception e) {
                throw new EventException(e);
            }
        }
    }
}
