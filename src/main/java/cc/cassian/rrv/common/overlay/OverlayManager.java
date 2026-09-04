package cc.cassian.rrv.common.overlay;

import cc.cassian.rrv.api.client.ExclusionAreaManager;
import cc.cassian.rrv.api.event.OverlayManagementEvents;
import cc.cassian.rrv.client.util.RRVInputUtil;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/// CLIENT-ONLY
public class OverlayManager implements ExclusionAreaManager {

    public static final OverlayManager INSTANCE = new OverlayManager();

    private AbstractRrvOverlay.InventoryPositionInfo currentInvInfo = null;

    private final List<GuiEventListener> oldWidgets = new ArrayList<>();
    private final HashMap<AbstractRrvOverlay, AbstractRrvOverlay.ScreenContext> screenContextMap = new HashMap<>();
    private boolean queuedWidgetUpdate = false;
    private boolean newScreenQueued = false;

    public static List<OverlayManagementEvents.AddExclusionAreasEvent> EXCLUSION_AREA_EVENTS = new ArrayList<>();
    private final List<BlockingGuiComponent> exclusionAreas = new ArrayList<>();

    public boolean hasQueuedWidgetUpdate() {
        return this.queuedWidgetUpdate;
    }

    public void setQueuedWidgetUpdate(boolean queuedWidgetUpdate) {
        this.queuedWidgetUpdate = queuedWidgetUpdate;
    }

    public void setCurrentInvInfo(AbstractRrvOverlay.InventoryPositionInfo info) {
        this.currentInvInfo = info;
        this.newScreenQueued = true;
    }

    public boolean checkForScreenChange(AbstractRrvOverlay.InventoryPositionInfo newInfo) {
        if (newInfo != null && (!newInfo.matches(this.currentInvInfo))) {
            this.setCurrentInvInfo(newInfo);
            return true;
        }

        // Must return true if screen changed and components aren't updated
        return this.newScreenQueued;
    }

    /// Update all overlays and collect widgets
    public void onScreenChanged() {
        PRESENT_OVERLAYS.forEach(overlay -> overlay.onScreenChanged(this.currentInfo()));

        PRESENT_OVERLAYS.forEach(present -> {
            AbstractRrvOverlay.ScreenContext screenContext = new AbstractRrvOverlay.ScreenContext();
            present.placeWidgets(screenContext);
            this.screenContextMap.put(present, screenContext);
        });

    }

    /// Update widget lists
    public void updateOverlaysAndWidgets(boolean always) {
        if (!always && !this.newScreenQueued)
            return;
        this.newScreenQueued = false;
        if (this.currentInfo() == null)
            return;

        this.screenContextMap.forEach((overlay, screenContext) -> {
            screenContext.renderables().stream().filter(guiEventListener -> !oldWidgets.contains(guiEventListener)).forEach(this.oldWidgets::add);
            screenContext.nonRenderables().stream().filter(guiEventListener -> !oldWidgets.contains(guiEventListener)).forEach(this.oldWidgets::add);
        });

        this.screenContextMap.clear();
        OverlayManager.INSTANCE.onScreenChanged();

        this.setQueuedWidgetUpdate(true);

    }

    public AbstractRrvOverlay.InventoryPositionInfo currentInfo() {
        return this.currentInvInfo;
    }

    /// Returns whether an [EditBox] overlay widget is focused
    public boolean isTextWidgetFocused() {

        if (this.currentInvInfo.screen().getFocused() == null)
            return false;

        if (!this.currentInvInfo.screen().getFocused().isFocused())
            return false;

        if (!(this.currentInvInfo.screen().getFocused() instanceof EditBox box))
            return false;

        if (this.screenContextMap.values().stream().anyMatch(screenContext -> screenContext.renderables().stream().filter(eventListener -> eventListener instanceof EditBox).anyMatch(eventListener -> ((EditBox) eventListener).getMessage().equals(box.getMessage()))))
            return true;

        return this.screenContextMap.values().stream().anyMatch(screenContext -> screenContext.nonRenderables().stream().filter(eventListener -> eventListener instanceof EditBox).anyMatch(eventListener -> ((EditBox) eventListener).getMessage().equals(box.getMessage())));
    }


    public HashMap<AbstractRrvOverlay, AbstractRrvOverlay.ScreenContext> screenContextMap() {
        return this.screenContextMap;
    }

    //Returns the list of old widgets that should be removed on the next widget update
    public List<GuiEventListener> oldWidgets() {
        return this.oldWidgets;
    }

    public boolean keyPressed(KeyEvent event) {
        boolean b = false;

        if (ReliableRecipeViewerClient.TOGGLE_OVERLAY_KEYBIND.matches(event)) {
            toggleOverlays();
            return true;
        }

        for (AbstractRrvOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled() || !overlay.isEnoughSpaceToRender())
                continue;

            if (overlay.keyPressed(event))
                b = true;
        }

        return b;
    }

    public static void toggleOverlays() {
        if (ItemViewOverlay.INSTANCE.isEnabled()) setOverlays(OverlayDisplay.DISABLED);
        else setOverlays(OverlayDisplay.ENABLED);
    }

    public static void setOverlays(OverlayDisplay enabled) {
        Configs.CLIENT_SETTINGS.setShowItemView(enabled);
        ItemViewOverlay.INSTANCE.setButtonVisibility(!enabled.equals(OverlayDisplay.DISABLED));
    }

    public static OverlayDisplay checkOverlays() {
        AtomicBoolean b = new AtomicBoolean(false);
        PRESENT_OVERLAYS.forEach(abstractRrvOverlay -> b.set(abstractRrvOverlay.isEnabled()));
        if (b.get()) {
            return OverlayDisplay.ENABLED;
        } else {
            return OverlayDisplay.DISABLED;
        }
    }

    public boolean charTyped(char c, int i) {
        boolean b = false;

        for (AbstractRrvOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled() || !overlay.isEnoughSpaceToRender())
                continue;

            if (overlay.charTyped(c, i))
                b = true;
        }

        return b;
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean b = false;


        this.screenContextMap.forEach((abstractRrvOverlay, screenContext) -> {
            screenContext.renderables().forEach(guiEventListener -> {
                if (guiEventListener.isFocused() && !guiEventListener.isMouseOver(event.x(), event.y()))
                    guiEventListener.setFocused(false);
                if (guiEventListener.isMouseOver(event.x(), event.y()) && RRVInputUtil.isLeftClick(event))
                    guiEventListener.setFocused(true);
            });
        });


        for (AbstractRrvOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled() || !overlay.isEnoughSpaceToRender())
                continue;

            if (!overlay.isMouseOver((int) event.x(), (int) event.y()))
                continue;

            if (overlay.mouseClicked(event, doubleClick))
                b = true;
        }

        return b;
    }

    public boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY) {
        boolean b = false;
        for (AbstractRrvOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled() || !overlay.isEnoughSpaceToRender())
                continue;

            if (!(mouseX >= overlay.getX() && mouseX <= overlay.getX() + overlay.getWidth() && mouseY >= overlay.getY() && mouseY <= overlay.getY() + overlay.getHeight()))
                continue;

            if (overlay.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
                b = true;
        }

        return b;
    }


    public void renderAllBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (Configs.CLIENT_SETTINGS.drawBackground())
            PRESENT_OVERLAYS.stream().filter(AbstractRrvOverlay::isEnabled).forEach(overlay -> {
                if (overlay.needsBackground())
				    overlay.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
                overlay.setNeedsBackground(false);
			});
    }

    public void renderAll(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PRESENT_OVERLAYS.stream().filter(AbstractRrvOverlay::isEnabled).forEach(overlay -> {
			overlay.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            overlay.setNeedsBackground(true);
		});

        if (RRVClientUtil.showDebugScreen())
            this.renderDebug(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void renderDebug(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

		ArrayList<net.minecraft.network.chat.Component> underMouse = new ArrayList<>();
        Font font = Minecraft.getInstance().font;
        this.exclusionAreas.forEach(blockingGuiComponent -> {
            String name = blockingGuiComponent.id().toString();
            guiGraphics.text(font, name, blockingGuiComponent.x(), blockingGuiComponent.y(), -1);

            Random rand = new Random(name.chars().sum());
            int debugColor = new Color(rand.nextInt(255 + 1), rand.nextInt(255 + 1), rand.nextInt(255 + 1)).getRGB();

            guiGraphics.horizontalLine(blockingGuiComponent.x(), blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y(), debugColor);
            guiGraphics.horizontalLine(blockingGuiComponent.x(), blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);

            guiGraphics.verticalLine(blockingGuiComponent.x(), blockingGuiComponent.y(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);
            guiGraphics.verticalLine(blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);
            if (blockingGuiComponent.hasIntersectionWith(mouseX, mouseY, 8, 8)) {
                underMouse.add(net.minecraft.network.chat.Component.literal(name));
            }
        });

        if (!underMouse.isEmpty()) {
            guiGraphics.setComponentTooltipForNextFrame(font, underMouse, mouseX, mouseY);
        }

    }


    /// See {@link OverlayManager#removeExclusionArea(Identifier, boolean)}
    @Deprecated(since = "8.10.0")
    public void removeGuiBlocking(Identifier id, boolean updateOverlays) {
        removeExclusionArea(id, updateOverlays);
    }

    public void removeExclusionArea(Identifier id, boolean updateOverlays) {
        this.exclusionAreas.removeIf(blockingGuiComponent -> blockingGuiComponent.id().equals(id));

        if (updateOverlays) {
            this.updateOverlaysAndWidgets(true);
        }

    }

    /// See {@link OverlayManager#removeExclusionArea(Predicate, boolean)}
    @Deprecated
    public void removeGuiBlocking(Predicate<Identifier> filter, boolean updateOverlays) {
        removeExclusionArea(filter, updateOverlays);
    }

    public void removeExclusionArea(Predicate<Identifier> filter, boolean updateOverlays) {
        this.exclusionAreas.removeIf(blockingGuiComponent -> filter.test(blockingGuiComponent.id()));

        if (updateOverlays) {
            this.updateOverlaysAndWidgets(true);
        }

    }

    /// Deprecated, see {@link #setExclusionArea(BlockingGuiComponent)}
    @Deprecated(since = "8.10.0")
    public void setGuiBlocking(BlockingGuiComponent area) {
        setExclusionArea(area);
    }

    public void setExclusionArea(BlockingGuiComponent area) {
        HashSet<BlockingGuiComponent> old = new HashSet<>(this.exclusionAreas);
        this.removeExclusionArea(area.id(), false);
        this.exclusionAreas.add(area);

        if (!(old.containsAll(this.exclusionAreas) && old.size() == this.exclusionAreas.size())) {
            this.setQueuedWidgetUpdate(true);
        }
    }


    /// See {@link #exclusionAreas}
    @Deprecated(since = "8.10.0")
    public List<BlockingGuiComponent> allGuiBlockings() {
        return exclusionAreas();
    }

    @Override
    public List<BlockingGuiComponent> exclusionAreas() {
        return this.exclusionAreas;
    }


    private static final List<AbstractRrvOverlay> PRESENT_OVERLAYS = new ArrayList<>();

    public static void registerOverlay(AbstractRrvOverlay overlay) {
        PRESENT_OVERLAYS.add(overlay);
    }


}
