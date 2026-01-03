package de.crafty.eiv.common.rendering;

/**
 * Interface used to allow the rendering of multiple entities in one frame
 * <br>
 * Wrapped to make sure EIV won't affect the rendering of other mods
 */
public interface IEivWrappedRenderState {


    void extendedItemView$enableMultiRendering();

    boolean extendedItemView$isMultiRenderingEnabled();
}
