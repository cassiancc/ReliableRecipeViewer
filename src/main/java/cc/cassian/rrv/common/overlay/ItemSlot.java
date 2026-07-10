package cc.cassian.rrv.common.overlay;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.stackgroup.data.AbstractStackGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/// Representation of one slot later rendered in the overlay
public class ItemSlot {

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");
    public static final int ITEM_ENTRY_SIZE = 19;

    private final ItemStack stack;
    private final int x, y;

    private boolean hovered;

    private int currentCheatmodeCount = 1;

    public ItemSlot(ItemStack stack, int x, int y) {
        this.stack = stack;

        this.x = x;
        this.y = y;
    }

    public void changeCheatmodeCount(int change) {
        this.currentCheatmodeCount += change;

        this.currentCheatmodeCount = Math.clamp(this.currentCheatmodeCount, 1, this.stack.getMaxStackSize());
    }

    /// @return The [ItemStack] that is currently hold by this slot
    public ItemStack getStack() {
        return this.stack;
    }

    public static List<ItemSlot> currentFrameSlots = null;

    private boolean hasGroupNeighbor(int dx, int dy, AbstractStackGroup group) {
        if (currentFrameSlots == null || group == null) return false;
        int targetX = this.x + dx;
        int targetY = this.y + dy;
        for (ItemSlot slot : currentFrameSlots) {
            if (slot.x == targetX && slot.y == targetY) {
                String otherGroupId = null;
                if (slot.stack.has(DataComponents.CUSTOM_DATA)) {
                    CompoundTag compoundTag = slot.stack.get(DataComponents.CUSTOM_DATA).copyTag();
                    if (compoundTag.contains("rrv_stack_group_id")) {
                        otherGroupId = compoundTag.get("rrv_stack_group_id").asString().get();
                    }
                }
                AbstractStackGroup otherGroup = otherGroupId != null ? StackGroupManager.getGroup(otherGroupId) : StackGroupManager.getGroupForItem(slot.getStack());
                if (otherGroup != null && otherGroup.getId().equals(group.getId()) && otherGroup.isEnabled) {
                    if (otherGroupId != null) {
                        return StackGroupManager.isEffectivelyExpanded(Identifier.parse(otherGroupId));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void drawGroupBackgroundAndBorders(GuiGraphicsExtractor guiGraphics, AbstractStackGroup group) {
        if (group == null) return;
        if (StackGroupManager.isSearchExpandActive()) return;
        if (ItemSlot.currentFrameSlots != ItemViewOverlay.INSTANCE.itemSlots()) return;

        boolean hasTop = hasGroupNeighbor(0, -ITEM_ENTRY_SIZE, group);
        boolean hasBottom = hasGroupNeighbor(0, ITEM_ENTRY_SIZE, group);
        boolean hasLeft = hasGroupNeighbor(-ITEM_ENTRY_SIZE, 0, group);
        boolean hasRight = hasGroupNeighbor(ITEM_ENTRY_SIZE, 0, group);

        int bgX1 = this.x;
        int bgY1 = this.y;
        int bgX2 = this.x + ITEM_ENTRY_SIZE;
        int bgY2 = this.y + ITEM_ENTRY_SIZE;

        guiGraphics.fill(bgX1, bgY1, bgX2, bgY2, 0x1AFFFFFF);

        int borderCol = 0x66FFFFFF;
        if (!hasTop) {
            guiGraphics.fill(bgX1, this.y, bgX2, this.y + 1, borderCol);
        }
        if (!hasLeft) {
            guiGraphics.fill(this.x, bgY1, this.x + 1, bgY2, borderCol);
        }
        if (!hasRight) {
            guiGraphics.fill(this.x + ITEM_ENTRY_SIZE - 1, bgY1, this.x + ITEM_ENTRY_SIZE, bgY2, borderCol);
        }
        if (!hasBottom) {
            guiGraphics.fill(bgX1, this.y + ITEM_ENTRY_SIZE - 1, bgX2, this.y + ITEM_ENTRY_SIZE, borderCol);
        }
    }

    /// Renders the slot
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.hovered = this.isMouseOver(mouseX, mouseY);

        if (!this.isHovered() && this.currentCheatmodeCount > 1)
            this.currentCheatmodeCount = 1;

        Minecraft mc = Minecraft.getInstance();
        List<Component> tooltip = new ArrayList<>();

        String recipe = null;
        String stackGroupId = null;

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag compoundTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (compoundTag.contains("rrv_result")) {
                recipe = compoundTag.get("rrv_result").asString().get();
            }
            if (compoundTag.contains("rrv_stack_group_id")) {
                stackGroupId = compoundTag.get("rrv_stack_group_id").asString().get();
            }
        }

        if (stackGroupId != null) {
            List<ItemStack> items = StackGroupManager.getGroupItems(stackGroupId);
            boolean expanded = StackGroupManager.isEffectivelyExpanded(Identifier.parse(stackGroupId));

            if (this.isHovered()) {
                AbstractStackGroup group = StackGroupManager.getGroup(stackGroupId);
                if (group != null) {
                    tooltip.add(group.getName().copy().withStyle(ChatFormatting.BLUE));
                    tooltip.add(Component.translatable("rrv.stack_group.tooltip.count", items.size()).withStyle(ChatFormatting.GRAY));
                    if (!ItemViewOverlay.INSTANCE.isSearchingStackGroups())
                        tooltip.add(Component.translatable(expanded ? "rrv.stack_group.tooltip.collapse" : "rrv.stack_group.tooltip.expand").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                    ReliableRecipeViewerClient.addNamespaceTooltip(RRVPlatform.INSTANCE.getModNameForNamespace(group.getId().getNamespace()), tooltip, true);
                }
                guiGraphics.fill(this.x, this.y, this.x + ITEM_ENTRY_SIZE, this.y + ITEM_ENTRY_SIZE, new Color(255, 255, 255, 32).getRGB());
            }

            if (expanded) {
                drawGroupBackgroundAndBorders(guiGraphics, StackGroupManager.getGroup(stackGroupId));
            }

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(this.x + 2 + 1.6F, this.y + 2 + 1.6F);
            guiGraphics.pose().scale(0.8F, 0.8F);

            if (items.size() == 1) {
                guiGraphics.fakeItem(items.getFirst(), 0, 0);
            } else if (items.size() == 2) {
                guiGraphics.pose().translate(0.5F, 0F);
                guiGraphics.fakeItem(items.get(1), 1, -1);
                guiGraphics.pose().translate(0F, 0F);
                guiGraphics.fakeItem(items.get(0), -2, 1);
            } else if (items.size() >= 3) {
                guiGraphics.fakeItem(items.get(2), 3, -2);
                guiGraphics.pose().translate(0F, 0F);
                guiGraphics.fakeItem(items.get(1), 0, 0);
                guiGraphics.pose().translate(0F, 0F);
                guiGraphics.fakeItem(items.get(0), -3, 2);
            }
            guiGraphics.pose().popMatrix();

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of(expanded ? "minus" : "plus"), this.x + 19 - 7, this.y + 19 - 7, 7, 7);

            if (this.isHovered()) {
                guiGraphics.setComponentTooltipForNextFrame(mc.font, tooltip, mouseX, mouseY);
            }
            return;
        }

        AbstractStackGroup itemGroup = StackGroupManager.getGroupForItem(this.stack);
        boolean inExpandedGroup = itemGroup != null && itemGroup.isEnabled && StackGroupManager.isEffectivelyExpanded(itemGroup.getId());

        if (inExpandedGroup) {
            drawGroupBackgroundAndBorders(guiGraphics, itemGroup);
        }

        if (this.isHovered()) {
            tooltip.addAll(Screen.getTooltipFromItem(mc, this.stack));

            ReliableRecipeViewerClient.addNamespaceTooltip(stack, tooltip, true);

            if (recipe != null) {
                tooltip.add(Component.translatable("view.rrv.recipe_id", Component.literal(recipe).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
            }

            if (ReliableRecipeViewerClient.isCheatmodeActive()) {
                MutableComponent count = Component.literal(String.valueOf(this.currentCheatmodeCount)).withStyle(ChatFormatting.GOLD);
                tooltip.addLast(Component.translatable("cheatmode.rrv.taking", count).withStyle(ChatFormatting.GRAY));
            }

            guiGraphics.fill(this.x, this.y, this.x + ITEM_ENTRY_SIZE, this.y + ITEM_ENTRY_SIZE, new Color(255, 255, 255, 32).getRGB());
        }
        guiGraphics.fakeItem(this.stack, this.x + 2, this.y + 2);

        // render recipe
        if (recipe != null) {
            guiGraphics.itemDecorations(mc.font, this.stack, this.x+2, this.y+2);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("recipe_stack_highlight"), this.x + 3, this.y + 2, 16, 16);
        }

        if (this.isHovered())
            guiGraphics.setComponentTooltipForNextFrame(mc.font, tooltip, mouseX, mouseY);
    }

    /// Called on a mouse click in any inventory
    public void onClicked(MouseButtonEvent event) {
        var mouseButton = event.button();

        LocalPlayer clientPlayer = Minecraft.getInstance().player;

        if (clientPlayer == null)
            return;

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag compoundTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (compoundTag.contains("rrv_stack_group_id")) {
                if (!StackGroupManager.isSearchExpandActive()) {
                    String groupId = compoundTag.get("rrv_stack_group_id").asString().get();
                    StackGroupManager.toggleGroup(Identifier.parse(groupId));
                    ItemViewOverlay.INSTANCE.updateDisplayedItems();
                    if (SidePanelOverlay.INSTANCE.isEnabled()) {
                        SidePanelOverlay.INSTANCE.updateSidePanelIndex(SidePanelOverlay.Reason.OTHER);
                    }
                }
                return;
            }
        }

        if (mouseButton == 2 && ReliableRecipeViewerClient.isCheatmodeActive()) {
            this.currentCheatmodeCount = this.stack.getMaxStackSize();
        }

        if (mouseButton == 0 && ReliableRecipeViewerClient.isCheatmodeActive()) {
            ClientNetworkManager.sendPacketToServer(new ServerboundPickCheatmodeItemPayload(this.stack.copy(), this.currentCheatmodeCount));
            return;
        }

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag compoundTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (compoundTag.contains("rrv_result")) {
                Identifier id = Identifier.parse(compoundTag.get("rrv_result").asString().get());
                ItemViewOverlay.INSTANCE.openRecipeView(id, Minecraft.getInstance().hasControlDown());
                return;
            }
        }

        if (mouseButton == 0)
            ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.RESULT);

        if (mouseButton == 1)
            ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.INPUT);
    }

    boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX < this.x + ITEM_ENTRY_SIZE && mouseY >= this.y && mouseY < this.y + ITEM_ENTRY_SIZE;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
