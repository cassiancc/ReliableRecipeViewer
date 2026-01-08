package cc.cassian.rrv.api;

import cc.cassian.rrv.api.recipe.ItemView;

/**
 * Hook for RRV integration in a client source set.
 */
@FunctionalInterface
public interface ReliableRecipeViewerClientPlugin {


    /**
     * Called once on game launch to register client recipe wrappers ({@link cc.cassian.rrv.api.recipe.ItemView#addClientRecipeWrapper}) and client reload callbacks ({@link cc.cassian.rrv.api.recipe.ItemView#addClientReloadCallback}).
     */
    void onIntegrationInitialize();
}
