package cc.cassian.rrv.common.gui.list;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.gui.StackGroupConfigScreen;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class StackGroupGridList extends GridList<StackGroup> {
    private final Set<String> disabledStackGroups;
    private String searchQuery = "";

    public StackGroupGridList(StackGroupConfigScreen screen, Set<String> disabledStackGroups) {
        super(screen);
        this.disabledStackGroups = disabledStackGroups;
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }

    @Override
    public Collection<StackGroup> getContents() {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return StackGroupManager.stackGroups;
        }

        List<StackGroup> filtered = new ArrayList<>();
        for (StackGroup group : StackGroupManager.stackGroups) {
            String name = group.getName() != null ? group.getName().getString().toLowerCase(Locale.ROOT) : "";
            String id = group.getId().toString().toLowerCase(Locale.ROOT);
            if (name.contains(searchQuery) || id.contains(searchQuery)) {
                filtered.add(group);
            }
        }
        return filtered;
    }

    @Override
    public ListEntry getEntryForContent(StackGroup content, TripleEntry<StackGroup> triple) {
        return new StackGroupEntry(content, triple, disabledStackGroups);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (StackGroupEntry.activeExpandedEntry != null) {
            if (StackGroupEntry.activeExpandedEntry.handleDropdownClick(event.x(), event.y())) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (StackGroupEntry.activeExpandedEntry != null && StackGroupEntry.activeExpandedEntry.draggedIndex != -1) {
            StackGroupEntry.activeExpandedEntry.handleDropdownRelease(event.x(), event.y(), event.button());
            return true;
        }
        return super.mouseReleased(event);
    }

    public static class StackGroupEntry extends ListEntry {
        public static StackGroupEntry activeExpandedEntry = null;
        private final StackGroup group;
        private final Switch switchWidget;
        private final List<AbstractWidget> childWidgets;
        public boolean isTitleHovered = false;
        public int draggedIndex = -1;
        public boolean isExpanded = false;

        public StackGroupEntry(StackGroup group, TripleEntry<StackGroup> triple, Set<String> disabledGroups) {
            super(triple);
            this.group = group;
            boolean checked = group != null && !disabledGroups.contains(group.getId().toString());

            this.switchWidget = new Switch.Builder(Component.empty())
                    .setChecked(checked)
                    .onCheckedChangeListener((_, isChecked) -> {
                        if (group != null) {
                            if (isChecked) disabledGroups.remove(group.getId().toString());
                            else disabledGroups.add(group.getId().toString());
                        }
                    })
                    .build();

            this.childWidgets = new ArrayList<>(List.of(switchWidget));
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (super.mouseClicked(event, doubleClick)) return true;
            if (event.button() != 0 || group == null) return false;

            List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());
            if (items.isEmpty()) return false;

            boolean canExpand = items.size() > 8;
            if (canExpand) {
                var font = Minecraft.getInstance().font;
                int titleX = getX() + BORDER_WIDTH + PADDING;
                int titleY = getY() + BORDER_WIDTH + PADDING + 2;

                if (event.x() >= titleX && event.x() <= titleX + font.width(getEntryTitle()) &&
                        event.y() >= titleY && event.y() <= titleY + font.lineHeight) {

                    isExpanded = !isExpanded;
                    if (isExpanded) {
                        if (activeExpandedEntry != null && activeExpandedEntry != this) {
                            activeExpandedEntry.isExpanded = false;
                        }
                        activeExpandedEntry = this;
                    } else if (activeExpandedEntry == this) {
                        activeExpandedEntry = null;
                    }
                    return true;
                }
            }

            if (!isExpanded) {
                int startX = getX() + BORDER_WIDTH + PADDING;
                int itemY = getY() + BORDER_WIDTH + PADDING + 18;
                for (int i = 0; i < Math.min(8, items.size()); i++) {
                    int itemX = startX + i * 18;
                    if (event.x() >= itemX && event.x() < itemX + 18 &&
                            event.y() >= itemY && event.y() < itemY + 18) {
                        draggedIndex = i;
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean handleDropdownClick(double mouseX, double mouseY) {
            if (!isExpanded || group == null) return false;
            List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());
            if (items.isEmpty()) return false;

            int startX = getX() + BORDER_WIDTH + PADDING;
            int itemY = getY() + BORDER_WIDTH + PADDING + 18;
            int totalItems = items.size();
            int rows = (int) Math.ceil(totalItems / 8.0);
            int dropDownHeight = rows * 18;

            if (mouseX >= startX && mouseX < startX + 8 * 18 &&
                    mouseY >= itemY && mouseY < itemY + dropDownHeight) {

                int col = (int) ((mouseX - startX) / 18);
                int row = (int) ((mouseY - itemY) / 18);
                int index = row * 8 + col;

                if (index >= 0 && index < totalItems) {
                    draggedIndex = index;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(@NonNull MouseButtonEvent event) {
            if (draggedIndex == -1 || event.button() != 0) return super.mouseReleased(event);

            if (!isExpanded) {
                List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());
                if (!items.isEmpty()) {
                    int startX = getX() + BORDER_WIDTH + PADDING;
                    int itemY = getY() + BORDER_WIDTH + PADDING + 18;

                    if (event.y() >= itemY - 10 && event.y() <= itemY + 18 + 10) {
                        int col = (int) Math.floor((event.x() - startX) / 18.0);
                        int dropIndex = Math.max(0, Math.min(col, Math.min(8, items.size()) - 1));

                        processItemReorder(items, dropIndex);
                    }
                }
            }
            draggedIndex = -1;
            return true;
        }

        public void handleDropdownRelease(double mouseX, double mouseY, int button) {
            if (draggedIndex == -1 || button != 0 || group == null) return;

            List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());
            if (!items.isEmpty()) {
                int startX = getX() + BORDER_WIDTH + PADDING;
                int itemY = getY() + BORDER_WIDTH + PADDING + 18;
                int totalItems = items.size();
                int rows = (int) Math.ceil(totalItems / 8.0);

                if (mouseY >= itemY - 10 && mouseY <= itemY + rows * 18 + 10) {
                    int col = (int) Math.floor((mouseX - startX) / 18.0);
                    int row = (int) Math.floor((mouseY - itemY) / 18.0);

                    if (col < 0) col = 0;
                    if (col > 7) col = 7;
                    if (row < 0) row = 0;
                    if (row >= rows) row = rows - 1;

                    int dropIndex = row * 8 + col;
                    dropIndex = Math.max(0, Math.min(dropIndex, totalItems - 1));

                    processItemReorder(items, dropIndex);
                }
            }
            draggedIndex = -1;
        }

        private void processItemReorder(List<ItemStack> items, int dropIndex) {
            if (dropIndex != draggedIndex && draggedIndex < items.size() && dropIndex < items.size()) {
                var item = items.remove(draggedIndex);
                items.add(dropIndex, item);

                List<String> newOrder = new ArrayList<>();
                for (var i : items) {
                    newOrder.add(BuiltInRegistries.ITEM.getKey(i.getItem()).toString());
                }
                Configs.CLIENT_SETTINGS.getStackGroupItemOrder().put(group.getId().toString(), newOrder);
                Configs.CLIENT_SETTINGS.save();
            }
        }

        @Override
        public void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int startX, int startY, float partialTick) {
            if (group == null) return;

            List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());
            if (items.isEmpty()) return;

            var font = Minecraft.getInstance().font;
            int totalItems = items.size();
            int itemY = startY + 18;
            int maxItems = isExpanded ? totalItems : Math.min(8, totalItems);
            int rows = isExpanded ? (int) Math.ceil(totalItems / 8.0) : 1;

            int currentDropIndex = draggedIndex;
            if (draggedIndex != -1 && mouseY >= itemY - 10 && mouseY <= itemY + rows * 18 + 10) {
                int col = (int) Math.floor((mouseX - startX) / 18.0);
                int row = isExpanded ? (int) Math.floor((mouseY - itemY) / 18.0) : 0;

                if (col < 0) col = 0;
                if (col > 7) col = 7;
                if (row < 0) row = 0;
                if (row >= rows) row = rows - 1;

                currentDropIndex = row * 8 + col;
                currentDropIndex = Math.max(0, Math.min(currentDropIndex, maxItems - 1));
            }

            var displayList = new ArrayList<>(items);
            if (draggedIndex != -1 && currentDropIndex != draggedIndex && currentDropIndex < displayList.size() && draggedIndex < displayList.size()) {
                var item = displayList.remove(draggedIndex);
                displayList.add(currentDropIndex, item);
            }

            guiGraphics.pose().pushMatrix();

            if (isExpanded) {
                guiGraphics.pose().translate(0, 0);
                int dropDownHeight = rows * 18;

                guiGraphics.fill(startX - 3, itemY - 3, startX + 8 * 18 + 3, itemY + dropDownHeight + 3, 0xFF555555);
                guiGraphics.fill(startX - 2, itemY - 2, startX + 8 * 18 + 2, itemY + dropDownHeight + 2, 0xFF1A1A1A);
            }

            for (int i = 0; i < maxItems; i++) {
                if (draggedIndex != -1 && i == currentDropIndex) continue;

                int col = i % 8;
                int row = i / 8;
                int itemX = startX + col * 18;
                int currentItemY = itemY + row * 18;
                var item = displayList.get(i);

                boolean hovered = draggedIndex == -1 &&
                        mouseX >= itemX && mouseX < itemX + 18 &&
                        mouseY >= currentItemY && mouseY < currentItemY + 18;

                guiGraphics.pose().pushMatrix();

                if (hovered) {
                    guiGraphics.pose().translate(itemX + 8, currentItemY + 8);
                    guiGraphics.pose().scale(1.2F, 1.2F);
                    guiGraphics.pose().translate(-(itemX + 8), -(currentItemY + 8));
                }

                guiGraphics.fakeItem(item, itemX, currentItemY);
                guiGraphics.itemDecorations(font, item, itemX, currentItemY);
                guiGraphics.pose().popMatrix();
            }
            guiGraphics.pose().popMatrix();

            if (draggedIndex != -1 && draggedIndex < items.size()) {
                var draggedItem = items.get(draggedIndex);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0, 0);
                guiGraphics.fakeItem(draggedItem, mouseX - 8, mouseY - 8);
                guiGraphics.itemDecorations(font, draggedItem, mouseX - 8, mouseY - 8);
                guiGraphics.pose().popMatrix();
            }
        }

        @Override
        protected Switch getSwitch() {
            return switchWidget;
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return childWidgets;
        }

        @Override
        public boolean shouldRenderSwitch() {
            return group != null;
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            var font = Minecraft.getInstance().font;
            int titleX = getX() + BORDER_WIDTH + PADDING;
            int titleY = getY() + BORDER_WIDTH + PADDING + 2;

            this.isTitleHovered = mouseX >= titleX && mouseX <= titleX + font.width(getEntryTitle()) &&
                    mouseY >= titleY && mouseY <= titleY + font.lineHeight;

            super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public Component getEntryTitle() {
            if (group == null) return Component.empty();

            Component baseName = group.getName();
            List<ItemStack> items = StackGroupManager.getGroupItems(group.getId().toString());

            if (items.size() > 8) {
                int color = isTitleHovered ? 0xFFFFFF : (isExpanded ? 0xFFAA00 : 0x00AAFF);

                return Component.literal(isExpanded ? "[-] " : "[+] ")
                        .append(baseName)
                        .withStyle(Style.EMPTY.withColor(color));
            }

            return baseName;
        }
    }
}
