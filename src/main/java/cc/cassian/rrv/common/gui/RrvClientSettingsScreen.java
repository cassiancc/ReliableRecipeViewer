package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public class RrvClientSettingsScreen extends Screen {

    private static final Component TITLE = Component.translatable("rrv.client_settings.title");

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public RrvClientSettingsScreen(Screen lastScreen) {
        super(TITLE);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {

        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        this.addRenderableWidget(stringWidget);

        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);

        addChild(linearLayout,"rrv.client_settings.itemview", Configs.CLIENT_SETTINGS.isShowOverlays(), OverlayDisplay.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setShowOverlays(sidePanel));
        addChild(linearLayout,"rrv.client_settings.sidepanel", Configs.CLIENT_SETTINGS.getSidePanel(), SidePanel.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setSidePanel(sidePanel));

        addChild(linearLayout, Component.translatable("rrv.client_settings.background.enabled"), Component.translatable("rrv.client_settings.background.disabled"), Configs.CLIENT_SETTINGS.drawBackground(), Component.translatable("rrv.client_settings.background"), (cycleButton, b )-> Configs.CLIENT_SETTINGS.setDrawBackground(b));
        addChild(linearLayout, Component.translatable("rrv.client_settings.resize_mode.wrap"), Component.translatable("rrv.client_settings.resize_mode.cut"), Configs.CLIENT_SETTINGS.isItemWrapMode(), Component.translatable("rrv.client_settings.resize_mode"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setItemWrapMode(b));
        addChild(linearLayout,"rrv.client_settings.wrap_scrolling", Configs.CLIENT_SETTINGS.isWrapScrolling(), WrapScrolling.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setWrapScrolling(sidePanel));

        addChild(linearLayout, Component.translatable("rrv.client_settings.append_namespace.show"), Component.translatable("rrv.client_settings.append_namespace.hide"), Configs.CLIENT_SETTINGS.isAppendModNamespace(), Component.translatable("rrv.client_settings.append_namespace"),(cycleButton, b) -> Configs.CLIENT_SETTINGS.setAppendModNamespace(b));
        addChild(linearLayout, Component.translatable("rrv.client_settings.right_index.right"), Component.translatable("rrv.client_settings.right_index.left"), Configs.CLIENT_SETTINGS.isRightIndex(), Component.translatable("rrv.client_settings.right_index"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setRightIndex(b));
        addChild(linearLayout, Component.translatable("rrv.client_settings.center_search.centered"), Component.translatable("rrv.client_settings.center_search.with_index"), Configs.CLIENT_SETTINGS.isCenterSearch(), Component.translatable("rrv.client_settings.center_search"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setCenterSearch(b));
        addChild(linearLayout, Component.translatable("rrv.client_settings.index_source.creative"), Component.translatable("rrv.client_settings.index_source.registry"), Configs.CLIENT_SETTINGS.isCreativeIndexSource(), Component.translatable("rrv.client_settings.index_source"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setCreativeIndexSource(b));

        Button button1 = this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).size(100, 20).build());
        this.addRenderableWidget(button1);

        ScrollableLayout scrollableLayout = this.layout.addToContents(new ScrollableLayout(Minecraft.getInstance(), linearLayout, 175));

        scrollableLayout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    private <T extends StringRepresentable> void addChild(LinearLayout linearLayout, String key, T initialValue, T[] values, CycleButton.OnValueChange<T> newValueSetter) {
        //? if >1.21.10 {
                linearLayout.addChild(
                CycleButton.builder((value)-> Component.translatable(key+"."+value.getSerializedName()), initialValue).withValues(values)
                        .create(0, 0, 250, 20, Component.translatable(key), newValueSetter)
        );
        //?} else {
        /*linearLayout.addChild(
                CycleButton.<T>builder((overlayDisplay)-> Component.translatable(key+"."+overlayDisplay.getSerializedName())).withInitialValue(initialValue).withValues(values)
                        .create(0, 0, 250, 20, Component.translatable(key), newValueSetter)
        );
        *///?}

	}

    private void addChild(LinearLayout linearLayout, MutableComponent enabled, MutableComponent disabled, boolean currentValue, MutableComponent translatable, CycleButton.OnValueChange<Boolean> newValueSetter) {
        //? >1.21.10 {
        linearLayout.addChild(CycleButton.booleanBuilder(enabled, disabled, currentValue).create(0, 0, 250, 20, translatable, newValueSetter));
        //?} else {
        /*linearLayout.addChild(CycleButton.booleanBuilder(enabled, disabled).withInitialValue(currentValue).create(0, 0, 250, 20, translatable, newValueSetter));
         *///?}
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Configs.CLIENT_SETTINGS.save();
        this.minecraft.setScreen(this.lastScreen);
    }
}
