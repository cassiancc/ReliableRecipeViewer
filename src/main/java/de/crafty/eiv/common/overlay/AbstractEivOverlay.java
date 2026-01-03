package de.crafty.eiv.common.overlay;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all overlays
 */
public abstract class AbstractEivOverlay {

    private final List<ItemSlot> itemSlots = new ArrayList<>();

    protected int x, y, width, height;

    //The effective dimensions after respecting all gui blockings
    protected int effectiveX, effectiveY, effectiveWidth, effectiveHeight;
    protected OverlayAlignment overlayAlignment;
    private boolean enabled, enoughSpaceToRender;

    protected AbstractEivOverlay(int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        this.x = defaultX;
        this.y = defaultY;
        this.width = defaultWidth;
        this.height = defaultHeight;

        this.overlayAlignment = OverlayAlignment.HORIZONTAL;

        this.enabled = true;
        this.enoughSpaceToRender = true;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isEnoughSpaceToRender() {
        return this.enoughSpaceToRender;
    }

    protected boolean keyPressed(KeyEvent keyEvent) {
        //Basic keybinds

        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (CommonEIVClient.USAGE_KEYBIND.matches(keyEvent))
                ItemViewOverlay.INSTANCE.openRecipeView(slot.getStack(), ItemViewOverlay.ItemViewOpenType.INPUT);

            if (CommonEIVClient.RECIPE_KEYBIND.matches(keyEvent))
                ItemViewOverlay.INSTANCE.openRecipeView(slot.getStack(), ItemViewOverlay.ItemViewOpenType.RESULT);

            return true;
        }

        return false;
    }

    protected boolean charTyped(char c, int i) {
        return false;
    }

    protected abstract boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick);

    protected abstract boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY);


    public List<ItemSlot> itemSlots() {
        return this.itemSlots;
    }

    public void onScreenChanged(InventoryPositionInfo info) {
        this.updateEffectiveDimensions(info);
    }


    protected void placeWidgets(ScreenContext ctx) {

    }


    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    protected abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);


    protected BlockingPredicate getBlockingPredicate() {
        return comp -> true;
    }


    protected boolean isPositionBlocked(int x, int y, int width, int height) {
        List<BlockingGuiComponent> relevantComponents = OverlayManager.INSTANCE.allGuiBlockings().stream().filter(blockingGuiComponent -> this.getBlockingPredicate().isBlocking(blockingGuiComponent)).toList();


        for (BlockingGuiComponent blocking : relevantComponents) {

            if (blocking.hasIntersectionWith(x, y, width, height))
                return true;
        }

        return false;
    }


    public void updateEffectiveDimensions(InventoryPositionInfo info) {
        this.effectiveX = this.x;
        this.effectiveY = this.y;

        this.effectiveWidth = this.width;
        this.effectiveHeight = this.height;


        if (OverlayManager.INSTANCE.allGuiBlockings().isEmpty())
            return;

        List<BlockingGuiComponent> relevantBlockings = OverlayManager.INSTANCE.allGuiBlockings().stream().filter(blockingGuiComponent -> blockingGuiComponent.hasIntersectionWith(this.x, this.y, this.width, this.height)).toList();
        if (relevantBlockings.isEmpty())
            return;

        //Calculating blocked square

        int mostLeftBlock = -1;
        int mostRightBlock = -1;
        int mostTopBlock = -1;
        int mostBottomBlock = -1;

        for (BlockingGuiComponent guiBlocking : relevantBlockings) {

            if (guiBlocking.x() < mostLeftBlock || mostLeftBlock < 0)
                mostLeftBlock = guiBlocking.x();

            if (guiBlocking.x() + guiBlocking.width() > mostRightBlock || mostRightBlock < 0)
                mostRightBlock = guiBlocking.x() + guiBlocking.width();

            if (guiBlocking.y() < mostTopBlock || mostTopBlock < 0)
                mostTopBlock = guiBlocking.y();

            if (guiBlocking.y() + guiBlocking.height() > mostBottomBlock || mostBottomBlock < 0)
                mostBottomBlock = guiBlocking.y() + guiBlocking.height();

        }

        if (this.overlayAlignment == OverlayAlignment.HORIZONTAL) {

            if (this.x <= info.screenWidth() / 2)
                this.effectiveWidth = mostLeftBlock - this.x;
            else {
                this.effectiveX = mostRightBlock;
                this.effectiveWidth = (this.x + this.width) - this.effectiveX;
            }

        }

        if (this.overlayAlignment == OverlayAlignment.VERTICAL) {
            if (this.y <= info.screenHeight() / 2)
                this.effectiveHeight = mostTopBlock - this.y;
            else {
                this.effectiveY = mostBottomBlock;
                this.effectiveHeight = (this.y + this.height) - this.effectiveY;
            }
        }

        this.enoughSpaceToRender = !(this.effectiveX < 0 || this.effectiveY < 0 || this.effectiveWidth <= 0 || this.effectiveHeight <= 0);
    }

    protected interface BlockingPredicate {

        boolean isBlocking(BlockingGuiComponent comp);

    }

    public record InventoryPositionInfo(AbstractContainerScreen<? extends AbstractContainerMenu> screen,
                                        int screenWidth, int screenHeight, int leftPos, int topPos, int imageWidth,
                                        int imageHeight) {

        public boolean matches(@Nullable InventoryPositionInfo info) {
            if (info == null)
                return false;

            return info.screen.equals(this.screen)
                    && info.screenWidth == this.screenWidth
                    && info.screenHeight == this.screenHeight
                    && info.leftPos == this.leftPos
                    && info.topPos == this.topPos
                    && info.imageWidth == this.imageWidth
                    && info.imageHeight == this.imageHeight;
        }

    }

    public static class ScreenContext {

        private final List<GuiEventListener> renderables;
        private final List<GuiEventListener> nonRenderables;

        public ScreenContext() {
            this.renderables = new ArrayList<>();
            this.nonRenderables = new ArrayList<>();
        }

        public <T extends GuiEventListener & Renderable & NarratableEntry> void addRenderable(T renderable) {
            if (renderable != null)
                this.renderables.add(renderable);
        }

        public <T extends GuiEventListener & NarratableEntry> void addNonRendarable(T nonRenderable) {
            if (nonRenderable != null)
                this.nonRenderables.add(nonRenderable);
        }


        public List<GuiEventListener> renderables() {
            return this.renderables;
        }

        public List<GuiEventListener> nonRenderables() {
            return this.nonRenderables;
        }

    }


    public enum OverlayAlignment {
        HORIZONTAL, VERTICAL;
    }
}
