package cc.cassian.rrv.common.rendering;

/**
 * Interface used to allow the rendering of multiple entities in one frame
 * <br>
 * Wrapped to make sure RRV won't affect the rendering of other mods
 */
public interface IRrvWrappedRenderState {


    void rrv$enableMultiRendering();

    boolean rrv$isMultiRenderingEnabled();
}
