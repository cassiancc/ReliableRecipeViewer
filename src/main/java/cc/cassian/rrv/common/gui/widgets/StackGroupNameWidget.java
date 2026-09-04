package cc.cassian.rrv.common.gui.widgets;

import cc.cassian.rrv.client.util.RRVInputUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.stackgroup.data.AbstractStackGroup;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/// The clickable group name shown in the stack group config screen
public class StackGroupNameWidget extends AbstractWidget {
    private static final int ENTRY_SIZE = ItemSlot.ITEM_ENTRY_SIZE;
    private static final int DROPDOWN_MARGIN = 8;

    private final Font font;
    private final AbstractStackGroup group;
    private final List<ItemStack> items;
    private final boolean isExpanded;
    private final Runnable onToggle;

    private boolean isTitleHovered = false;
    private int draggedIndex = -1;

    public StackGroupNameWidget(Font font, int width, int rowHeight, AbstractStackGroup group, boolean expanded, Runnable onToggle) {
        super(0, 0, width, rowHeight, group.getName());
        this.font = font;
        this.group = group;
        this.isExpanded = expanded;
        this.onToggle = onToggle;
        this.items = new ArrayList<>(StackGroupManager.getGroupItems(group.getId().toString()));
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private boolean canReorder() {
        return items.size() > 1;
    }

    private MutableComponent currentTitle() {
        Component name = group.getName();
        if (!canReorder()) return name.copy();
        return Component.literal(isExpanded ? "[-] " : "[+] ").append(name);
    }

    private int columns() {
        return columnsFor(this.width);
    }

    private int rowCount() {
        return rowsFor(items.size(), this.width);
    }

    private static int columnsFor(int width) {
        return Math.max(1, Math.min(12, width / ENTRY_SIZE));
    }

    private static int rowsFor(int itemCount, int width) {
        return Math.max(1, (int) Math.ceil(itemCount / (double) columnsFor(width)));
    }

    public static int dropdownHeightFor(int itemCount, int width) {
        return itemCount > 1 ? rowsFor(itemCount, width) * ENTRY_SIZE + DROPDOWN_MARGIN : 0;
    }

    private boolean titleContains(double mouseX, double mouseY) {
        int titleWidth = font.width(currentTitle());
        return mouseX >= getX() && mouseX < getX() + titleWidth && mouseY >= getY() && mouseY < getY() + this.height;
    }

    private boolean dropdownContains(double mouseX, double mouseY) {
        if (!isExpanded || items.isEmpty()) return false;
        int startX = getX();
        int startY = getY() + this.height;
        int dropdownWidth = columns() * ENTRY_SIZE;
        int dropdownHeight = rowCount() * ENTRY_SIZE;
        return mouseX >= startX - 3 && mouseX <= startX + dropdownWidth + 3 && mouseY >= startY - 3 && mouseY <= startY + dropdownHeight + 3;
    }

    /// Hit test used to start a drag (only claims an actual item slot)
    private int strictIndexAt(double mouseX, double mouseY) {
        if (!isExpanded || items.isEmpty()) return -1;
        int columns = columns();
        int startX = getX();
        int startY = getY() + this.height;
        int dropdownWidth = columns * ENTRY_SIZE;
        int dropdownHeight = rowCount() * ENTRY_SIZE;
        if (mouseX < startX || mouseX >= startX + dropdownWidth || mouseY < startY || mouseY >= startY + dropdownHeight) return -1;
        int col = (int) ((mouseX - startX) / ENTRY_SIZE);
        int row = (int) ((mouseY - startY) / ENTRY_SIZE);
        int index = row * columns + col;
        return index < items.size() ? index : -1;
    }

    /// Lenient hit test used while dragging/releasing, resolving to the nearest valid slot
    private int clampedIndexAt(double mouseX, double mouseY) {
        if (items.isEmpty()) return -1;
        int columns = columns();
        int startX = getX();
        int startY = getY() + this.height;
        int rows = rowCount();
        int dropdownHeight = rows * ENTRY_SIZE;

        if (mouseY < startY - 10 || mouseY > startY + dropdownHeight + 10) return -1;

        int col = (int) Math.floor((mouseX - startX) / (double) ENTRY_SIZE);
        int row = (int) Math.floor((mouseY - startY) / (double) ENTRY_SIZE);
        col = Math.max(0, Math.min(col, columns - 1));
        row = Math.max(0, Math.min(row, rows - 1));

        int index = row * columns + col;
        return Math.max(0, Math.min(index, items.size() - 1));
    }

    private void persistOrder() {
        List<String> order = items.stream()
                .map(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())
                .toList();
        ConfiguredStackGroup current = Configs.STACK_GROUPS.getOrDefault(group.getId());
        Configs.STACK_GROUPS.set(group.getId(), new ConfiguredStackGroup(group.getId(), current.enabled(), current.priority(), order));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isActive() && (titleContains(mouseX, mouseY) || dropdownContains(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.isActive() || event.button() != 0) return false;
        double mouseX = event.x();
        double mouseY = event.y();

        if (canReorder() && titleContains(mouseX, mouseY)) {
            if (onToggle != null) onToggle.run();
            return true;
        }

        if (dropdownContains(mouseX, mouseY)) {
            int index = strictIndexAt(mouseX, mouseY);
            if (index != -1) {
                draggedIndex = index;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggedIndex != -1 && RRVInputUtil.isLeftClick(event)) {
            int dropIndex = clampedIndexAt(event.x(), event.y());
            if (dropIndex != -1 && dropIndex != draggedIndex) {
                ItemStack moved = items.remove(draggedIndex);
                items.add(Math.min(dropIndex, items.size()), moved);
                persistOrder();
            }
            draggedIndex = -1;
            return true;
        }
        return false;
    }

    @Override
    //~ if >26 'renderWidget'-> 'extractWidgetRenderState'
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        isTitleHovered = titleContains(mouseX, mouseY);

        int color = 0xFFFFFFFF;
        if (canReorder()) {
            color = isTitleHovered ? 0xFFFFFFFF : (isExpanded ? 0xFFFFAA00 : 0xFF55CFFF);
        }

        MutableComponent display = currentTitle().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal(group.getId().toString()))));
        int textY = getY() + (this.height - font.lineHeight) / 2;
        guiGraphics.text(font, display, getX(), textY, color, true);
    }

    public void renderDropdownOverlay(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (!isExpanded || items.isEmpty()) return;

        int columns = columns();
        int rows = rowCount();
        int startX = getX();
        int startY = getY() + this.height;
        int dropdownWidth = columns * ENTRY_SIZE;
        int dropdownHeight = rows * ENTRY_SIZE;

        int previewDropIndex = draggedIndex;
        if (draggedIndex != -1) {
            int computed = clampedIndexAt(mouseX, mouseY);
            if (computed != -1) previewDropIndex = computed;
        }

        guiGraphics.fill(startX - 2, startY - 2, startX + dropdownWidth + 2, startY + dropdownHeight + 2, 0xFF1A1A1A);
        guiGraphics.fill(startX - 3, startY - 3, startX + dropdownWidth + 3, startY - 2, 0xFF555555);
        guiGraphics.fill(startX - 3, startY + dropdownHeight + 2, startX + dropdownWidth + 3, startY + dropdownHeight + 3, 0xFF555555);
        guiGraphics.fill(startX - 3, startY - 2, startX - 2, startY + dropdownHeight + 2, 0xFF555555);
        guiGraphics.fill(startX + dropdownWidth + 2, startY - 2, startX + dropdownWidth + 3, startY + dropdownHeight + 2, 0xFF555555);

        List<ItemStack> displayList = new ArrayList<>(items);
        if (draggedIndex != -1 && previewDropIndex != draggedIndex && previewDropIndex >= 0 && previewDropIndex < displayList.size()) {
            ItemStack moving = displayList.remove(draggedIndex);
            displayList.add(previewDropIndex, moving);
        }

        for (int i = 0; i < displayList.size(); i++) {
            if (draggedIndex != -1 && i == previewDropIndex) continue;
            int col = i % columns;
            int row = i / columns;
            int itemX = startX + col * ENTRY_SIZE;
            int itemY = startY + row * ENTRY_SIZE;
            guiGraphics.fakeItem(displayList.get(i), itemX + 2, itemY + 2);
        }

        if (draggedIndex != -1 && draggedIndex < items.size()) {
            guiGraphics.fakeItem(items.get(draggedIndex), mouseX - 8, mouseY - 8);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }
}
