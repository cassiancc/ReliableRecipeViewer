package cc.cassian.rrv.api;

import cc.cassian.rrv.api.recipe.ItemView;

/**
 * Hook for RRV integration in a main source set.
 */
@FunctionalInterface
public interface ReliableRecipeViewerPlugin {


    /**
     * Called once on game launch to register server recipe providers ({@link cc.cassian.rrv.api.recipe.ItemView#addServerRecipeProvider}) and server reload callbacks ({@link cc.cassian.rrv.api.recipe.ItemView#addServerReloadCallback}).
     * If you are not using split sources, this can also be used to register client recipe wrappers and client reload callbacks.
     */
    void onIntegrationInitialize();
}
