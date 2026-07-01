package cc.cassian.rrv.common.gui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ListEntry extends AbstractContainerWidget {
    protected static final int PADDING = 8;
    protected static final int BORDER_WIDTH = 1;

    public static final int WIDTH = 18 * 8 + PADDING * 2 + BORDER_WIDTH * 2;
    public static final int HEIGHT = 18 * 2 + PADDING * 2 + BORDER_WIDTH * 2;

    private static final ScrollbarSettings SCROLLBAR_SETTINGS = new ScrollbarSettings(
            Identifier.withDefaultNamespace("widget/scrollbar_track"),
            Identifier.withDefaultNamespace("widget/scrollbar_track_hovered"),
            Identifier.withDefaultNamespace("widget/scrollbar_grab"),
            6, 6, 2, true
    );

    private final GuiEventListener container;

    protected ListEntry(GuiEventListener container) {
        super(0, 0, WIDTH, HEIGHT, Component.empty(), SCROLLBAR_SETTINGS);
        this.container = container;
    }

    @Override
    public int contentHeight() {
        return HEIGHT;
    }

    protected abstract Switch getSwitch();

    protected abstract List<AbstractWidget> getChildren();

    public abstract boolean shouldRenderSwitch();

    public abstract Component getEntryTitle();

    public abstract void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int startX, int startY, float partialTick);

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int startX = getX() + BORDER_WIDTH + PADDING;
        int startY = getY() + BORDER_WIDTH + PADDING;

        int renderHeight = HEIGHT + 2;
        guiGraphics.fill(getX(), getY(), getX() + WIDTH, getY() + renderHeight, 0x3F000000);
        guiGraphics.fill(getX(), getY(), getX() + WIDTH, getY() + 2, 0xFF555555);
        guiGraphics.fill(getX(), getY() + renderHeight - 2, getX() + WIDTH, getY() + renderHeight, 0xFF555555);

        Component title = getEntryTitle();
        if (title != null) {
            var font = Minecraft.getInstance().font;
            guiGraphics.text(font, title, startX, startY + 2, 0xFFFFFF);
        }

        renderEntry(guiGraphics, mouseX, mouseY, startX, startY, partialTick);

        if (shouldRenderSwitch()) {
            Switch sw = getSwitch();
            sw.setX(getX() + WIDTH - sw.getWidth() - BORDER_WIDTH - PADDING);
            sw.setY(startY);
            sw.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isFocused() {
        if (container instanceof ContainerEventHandler ceh)
            return ceh.getFocused() == this;
        return false;
    }

    @Override
    public void setFocused(boolean isFocused) {
        if (!isFocused) {
            getChildren().forEach(w -> w.setFocused(false));
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return getChildren();
    }
}
