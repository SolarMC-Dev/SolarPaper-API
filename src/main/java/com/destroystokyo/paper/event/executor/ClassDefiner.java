package com.destroystokyo.paper.event.executor;

public interface ClassDefiner {

    /**
     * Defines a class. The purpose of this serves to define a class which can access the specified
     * {@code listenerClass}
     *
     * @param listenerClass the listener class which the defined class must be able to access
     * @param name         the name of the class
     * @param data         the class data to load
     * @return the defined class
     * @throws ClassFormatError     if the class data is invalid
     * @throws NullPointerException if any of the arguments are null
     */
    Class<?> defineClass(Class<?> listenerClass, String name, byte[] data);

    /**
     * Determines the name which will be used for the generated class file data
     *
     * @param listenerClass the listener class which the generated class must be able to access
     * @param uniqueId an integer unique to the generated class which may be used to deduplicate class names
     * @return the full name of the generated class
     */
    default String getDefinedClassName(Class<?> listenerClass, int uniqueId) {
        return getDefiningPackage() + ".GeneratedEventExecutor" + uniqueId;
    }

    /**
     * Determines the package in which the generated class file data will derive its class name
     *
     * @return the package in which to place the generated class
     * @deprecated Implementors should override {@link #getDefinedClassName(Class, int)}  instead
     */
    @Deprecated
    default String getDefiningPackage() {
        return "com.destroystokyo.paper.event.executor.asm.generated";
    }

}
