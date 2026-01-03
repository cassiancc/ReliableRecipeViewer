package de.crafty.eiv.common.gui;

import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EivClientSettingsScreen extends Screen {

    private static final Component TITLE = Component.translatable("eiv.client_settings.title");

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public EivClientSettingsScreen(Screen lastScreen) {
        super(TITLE);

        this.lastScreen = lastScreen;
    }


    @Override
    protected void init() {

        this.layout.addToHeader(new StringWidget(TITLE, this.font));

        LinearLayout linearLayout = this.layout.addToContents(LinearLayout.vertical().spacing(2));

        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.itemview.enabled"), Component.translatable("eiv.client_settings.itemview.disabled"), OverlayManager.checkOverlays())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.itemview"),
                                (cycleButton, b) -> OverlayManager.toggleOverlays())
        );

        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.background.enabled"), Component.translatable("eiv.client_settings.background.disabled"), Configs.CLIENT_SETTINGS.drawBackground())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.background"),
                                (cycleButton, b) -> Configs.CLIENT_SETTINGS.setDrawBackground(b))
        );
        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.resize_mode.wrap"), Component.translatable("eiv.client_settings.resize_mode.cut"), Configs.CLIENT_SETTINGS.isItemWrapMode())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.resize_mode"),
                                (cycleButton, b) -> Configs.CLIENT_SETTINGS.setItemWrapMode(b))
        );
        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.append_namespace.show"), Component.translatable("eiv.client_settings.append_namespace.hide"), Configs.CLIENT_SETTINGS.isAppendModNamespace())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.append_namespace"),
                                (cycleButton, b) -> Configs.CLIENT_SETTINGS.setAppendModNamespace(b))
        );
        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.right_index.right"), Component.translatable("eiv.client_settings.right_index.left"), Configs.CLIENT_SETTINGS.isRightIndex())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.right_index"),
                                (cycleButton, b) -> Configs.CLIENT_SETTINGS.setRightIndex(b))
        );
        linearLayout.addChild(
                CycleButton.booleanBuilder(Component.translatable("eiv.client_settings.center_search.centered"), Component.translatable("eiv.client_settings.center_search.with_index"), Configs.CLIENT_SETTINGS.isCenterSearch())
                        .create(0, 0, 250, 20, Component.translatable("eiv.client_settings.center_search"),
                                (cycleButton, b) -> Configs.CLIENT_SETTINGS.setCenterSearch(b))
        );

        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).size(100, 20).build());

        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }


    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
    }


    @Override
    public void onClose() {
        Configs.CLIENT_SETTINGS.save();
        this.minecraft.setScreen(this.lastScreen);
    }
}
