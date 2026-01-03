package de.crafty.eiv.common.api;

/**
 * Each mod should have their own EIV integration and register everything EIV related in there
 */
@FunctionalInterface
public interface IExtendedItemViewIntegration {


    /**
     * Called once on game launch to register callbacks, recipe providers, recipe wrappers, ...
     */
    void onIntegrationInitialize();
}
