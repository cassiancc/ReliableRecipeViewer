package cc.cassian.rrv.api.overlay;

import cc.cassian.rrv.common.overlay.ItemSlot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public final class OverlayView {
	private static final ArrayList<OverlayKeybindSlotHandler> GLOBAL_SLOT_KEYBIND_HANDLERS = new ArrayList<>();

	private OverlayView() {}

	public static final Identifier UNKNOWN = Identifier.fromNamespaceAndPath("rrv", "unknown");
	public static final Identifier ITEM_VIEW = Identifier.fromNamespaceAndPath("rrv", "item_view");
	public static final Identifier CRAFTABLES = Identifier.fromNamespaceAndPath("rrv", "craftables");
	public static final Identifier BOOKMARKS = Identifier.fromNamespaceAndPath("rrv", "bookmarks");

	public static void registerGlobalOverlayKeybindSlotHandler(OverlayKeybindSlotHandler handler) {
		GLOBAL_SLOT_KEYBIND_HANDLERS.add(handler);
	}

	public static void registerGlobalOverlayKeybindSlotHandler(KeyMapping keyMapping, OverlayKeybindSlotHandler handler) {
		GLOBAL_SLOT_KEYBIND_HANDLERS.add(new KeySpecificHandler(keyMapping, handler));
	}

	public static ArrayList<OverlayKeybindSlotHandler> getGlobalSlotKeybindHandlers() {
		return GLOBAL_SLOT_KEYBIND_HANDLERS;
	}

	private record KeySpecificHandler(KeyMapping keyMapping, OverlayKeybindSlotHandler handler)
			implements OverlayKeybindSlotHandler {
		@Override
		public boolean onKeybindPressedOnSlot(@NonNull KeyEvent keyEvent, @NonNull ItemSlot slot, @NonNull Identifier overlay) {
			return this.keyMapping.matches(keyEvent) && this.handler.onKeybindPressedOnSlot(keyEvent, slot, overlay);
		}
	}
}
