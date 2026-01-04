package cc.cassian.rrv.common.api;

/**
 * Each mod should have their own RRV integration and register everything RRV related in there
 */
@FunctionalInterface
public interface ReliableRecipeViewerPlugin {


    /**
     * Called once on game launch to register callbacks, recipe providers, recipe wrappers, ...
     */
    void onIntegrationInitialize();
}
