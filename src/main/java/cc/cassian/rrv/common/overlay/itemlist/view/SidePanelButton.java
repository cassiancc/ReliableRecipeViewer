package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

import static cc.cassian.rrv.common.config.options.SidePanel.*;

public class SidePanelButton extends ReliableSpriteIconButton {
    private static final Identifier SIDE_PANEL_TOGGLE = ReliableRecipeViewer.of("side_panel_button");

    public SidePanelButton() {
        super(18, getTooltip(), 14, SIDE_PANEL_TOGGLE, SidePanelButton::nextSidePanel);
    }

    public static void nextSidePanel(Button button) {
        if (Minecraft.getInstance().hasShiftDown()) {
            switch (Configs.CLIENT_SETTINGS.getSidePanel()) {
                case BOOKMARKS -> Configs.CLIENT_SETTINGS.setSidePanel(DISABLED);
                case CRAFTABLES -> Configs.CLIENT_SETTINGS.setSidePanel(BOOKMARKS);
                case DISABLED -> Configs.CLIENT_SETTINGS.setSidePanel(CRAFTABLES);
            }
        } else {
            switch (Configs.CLIENT_SETTINGS.getSidePanel()) {
                case BOOKMARKS -> Configs.CLIENT_SETTINGS.setSidePanel(CRAFTABLES);
                case CRAFTABLES -> Configs.CLIENT_SETTINGS.setSidePanel(DISABLED);
                case DISABLED -> Configs.CLIENT_SETTINGS.setSidePanel(BOOKMARKS);
            }
        }
        if (!Configs.CLIENT_SETTINGS.getSidePanel().equals(DISABLED)) {
            SidePanelOverlay.INSTANCE.updateSidePanelIndex(SidePanelOverlay.Reason.BUTTON);
        }
        button.setTooltip(Tooltip.create(getTooltip()));
    }

    private static MutableComponent getTooltip() {
        return Component.translatable("rrv.side_panel.btn", Component.translatable("rrv.client_settings.sidepanel." + Configs.CLIENT_SETTINGS.getSidePanel().name().toLowerCase(Locale.ROOT)));
    }

    @Override
    protected void extractSprite(final GuiGraphicsExtractor graphics, final int x, final int y) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("side_panel_"+ Configs.CLIENT_SETTINGS.getSidePanel().name().toLowerCase(Locale.ROOT)), x, y, this.spriteWidth, this.spriteHeight, this.alpha);
    }
}
