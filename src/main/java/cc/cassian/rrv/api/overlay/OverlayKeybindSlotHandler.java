package cc.cassian.rrv.api.overlay;

import cc.cassian.rrv.common.overlay.ItemSlot;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/// CLIENT-ONLY
@FunctionalInterface
public interface OverlayKeybindSlotHandler {
	/**
	 * Called when a keybind is pressed on a slot in an RRV overlay.
	 *
	 * @param keyEvent the key event that triggered the keybind
	 * @param slot the item slot where the keybind was pressed
	 * @param overlay the identifier of the overlay where the keybind was pressed
	 * @return true if the keybind was handled, false otherwise
	 */
	boolean onKeybindPressedOnSlot(@NonNull KeyEvent keyEvent,@NonNull ItemSlot slot,@NonNull Identifier overlay);
}
