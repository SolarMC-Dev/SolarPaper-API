package com.destroystokyo.paper.event.executor;

public final class ModuleOpener {

    /**
     * Opens the package of the listener class to the target module, taking advantage of
     * {@link Module#addOpens(String, Module)}
     *
     * @param listenerClass the listener class whose package to open
     * @param targetModule the module to open to
     */
    public void openPackage(Class<?> listenerClass, Module targetModule) {
        Module listenerModule = listenerClass.getModule();
        String packageName = listenerClass.getPackageName();
        try {
            listenerModule.addOpens(packageName, targetModule);
        } catch (IllegalCallerException ex) {
            throw new IllegalStateException(
                    "Listener " + listenerClass.getName() + " from " + listenerModule
                            + " must open package " + packageName + " to " + getClass().getModule(),
                    ex);
        }
    }

}
