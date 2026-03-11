package cc.cassian.rrv.common.overlay;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of one slot later rendered in the overlay
 */
public class ItemSlot {

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

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

        this.currentCheatmodeCount = Math.max(1, Math.min(this.currentCheatmodeCount, this.stack.getMaxStackSize()));
    }

    /**
     * @return The itemStack that is currently hold by this slot
     */
    public ItemStack getStack() {
        return this.stack;
    }

    /**
     * Renders the slot
     */
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.hovered = this.isMouseOver(mouseX, mouseY);

        if (!this.isHovered() && this.currentCheatmodeCount > 1)
            this.currentCheatmodeCount = 1;

        Minecraft mc = Minecraft.getInstance();
        List<Component> tooltip = new ArrayList<>();

        if (this.isHovered()) {

            tooltip.addAll(Screen.getTooltipFromItem(mc, this.stack));

            if (ReliableRecipeViewerClient.isCheatmodeActive()) {
                MutableComponent count = Component.literal(String.valueOf(this.currentCheatmodeCount)).withStyle(ChatFormatting.GOLD);
                tooltip.addLast(Component.translatable("cheatmode.rrv.taking", count).withStyle(ChatFormatting.GRAY));
            }

            if (Configs.CLIENT_SETTINGS.isAppendModNamespace())
                tooltip.addLast(Component.literal(ReliableRecipeViewerClient.resolver().getModNameForItem(this.stack)).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

            guiGraphics.fill(this.x, this.y, this.x + 20, this.y + 20, new Color(255, 255, 255, 32).getRGB());

        }
        guiGraphics.fakeItem(this.stack, this.x + 2, this.y + 2);


        if (this.isHovered())
            guiGraphics.setComponentTooltipForNextFrame(mc.font, tooltip, mouseX, mouseY);
    }

    /**
     * Called on a mouse click in any inventory
     */
    public void onClicked(MouseButtonEvent event) {
        var mouseButton = event.button();


        LocalPlayer clientPlayer = Minecraft.getInstance().player;

        if (clientPlayer == null)
            return;

        if (mouseButton == 2 && ReliableRecipeViewerClient.isCheatmodeActive()) {
            this.currentCheatmodeCount = this.stack.getMaxStackSize();
        }

        if (mouseButton == 0 && ReliableRecipeViewerClient.isCheatmodeActive()) {
            RrvClientNetworkManager.sendPacketToServer(new ServerboundPickCheatmodeItemPayload(this.stack.copy(), this.currentCheatmodeCount));
            return;
        }

        if (mouseButton == 0)
            ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.RESULT);

        if (mouseButton == 1)
            ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.INPUT);
    }

    boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX < this.x + 20 && mouseY >= this.y && mouseY < this.y + 20;
    }

    public boolean isHovered() {
        return this.hovered;
    }
}
