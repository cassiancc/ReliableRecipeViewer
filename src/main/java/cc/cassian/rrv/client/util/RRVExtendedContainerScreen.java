package cc.cassian.rrv.client.util;

import org.jetbrains.annotations.ApiStatus;

/**
 * This interface is used to give valid entry points for Mixins.
 */
@ApiStatus.Internal
public interface RRVExtendedContainerScreen {
	default void rrv$callInit() {
		throw new UnsupportedOperationException();
	}

	boolean rrv$triggerInitLater();
}
