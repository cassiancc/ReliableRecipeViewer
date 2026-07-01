package cc.cassian.rrv.common.gui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GridList<Contents> extends ContainerObjectSelectionList<GridList.TripleEntry<Contents>> {

    protected final Screen screen;

    protected GridList(Screen screen) {
        super(Minecraft.getInstance(), screen.width, screen.height, 0, TripleEntry.HEIGHT);
        this.screen = screen;
        centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public int getScrollbarPosition() {
        return this.width - 6;
    }

    public abstract Collection<Contents> getContents();

    public abstract ListEntry getEntryForContent(Contents content, TripleEntry<Contents> triple);

    public void add() {
        List<Contents> contents = new ArrayList<>(getContents());
        for (int i = 0; i < contents.size(); i += 3) {
            List<Contents> chunk = contents.subList(i, Math.min(i + 3, contents.size()));
            addEntry(new TripleEntry<>(this, chunk));
        }
    }

    public void refreshList() {
        this.clearEntries();
        this.add();
        this.setScrollAmount(0);
    }

    public static class TripleEntry<Contents> extends ContainerObjectSelectionList.Entry<TripleEntry<Contents>> {
        static final int GUTTER = 6;
        static final int WIDTH = ListEntry.WIDTH * 3 + GUTTER * 2;
        static final int HEIGHT = ListEntry.HEIGHT + GUTTER * 2;

        private final GridList<Contents> listWidget;
        private final List<ListEntry> children = new ArrayList<>();

        public TripleEntry(GridList<Contents> listWidget, List<Contents> contentsList) {
            this.listWidget = listWidget;
            for (int i = 0; i < 3; i++) {
                if (i < contentsList.size()) {
                    children.add(listWidget.getEntryForContent(contentsList.get(i), this));
                }
            }
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor guiGraphics, int top, int left, boolean isHovered, float partialTick) {
            double mouseX = Minecraft.getInstance().mouseHandler.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double) Minecraft.getInstance().getWindow().getWidth();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getHeight();

            int xOffset = 0;
            int startX = (listWidget.screen.width - WIDTH) / 2;
            for (ListEntry entry : children) {
                entry.setPosition(startX + xOffset, top);
                entry.extractWidgetRenderState(guiGraphics, (int) mouseX, (int) mouseY, partialTick);
                xOffset += ListEntry.WIDTH + GUTTER * 2;
            }
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return children;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return children;
        }
    }
}
