package com.destroystokyo.paper.event.executor;

/**
 * SPI for class definers which creates the actual definer when necessary
 *
 */
public interface ClassDefinerFactory {

    /**
     * Creates using the given module layer controller, which corresponds
     * to the plugin module layer
     *
     * @param layerController the layer controller
     * @return the class definer
     */
    ClassDefiner create(ModuleLayer.Controller layerController);

}
