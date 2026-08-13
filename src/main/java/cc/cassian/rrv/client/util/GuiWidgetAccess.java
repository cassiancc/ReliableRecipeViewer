package cc.cassian.rrv.client.util;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.ArrayList;

public interface GuiWidgetAccess {
	ArrayList<Renderable> widgets = new ArrayList<>();

	default <T extends GuiEventListener & Renderable & NarratableEntry> T addRecipeWidget(T widget) {
		widgets.add(widget);
		return RRVClientUtil.currentScreen().addRenderableWidget(widget);
	}

	default void clearRecipeWidgets() {
		widgets.forEach(r->{
			RRVClientUtil.currentScreen().removeWidget((GuiEventListener) r);
		});
	}

	default boolean isEmpty() {
		return widgets.isEmpty();
	}

}
