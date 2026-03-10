package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public class RrvClientSettingsScreen extends Screen {

    private static final Component TITLE = clientSetting("title");
    int yPos = 20;

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public RrvClientSettingsScreen(Screen lastScreen) {
        super(TITLE);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {

        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        stringWidget.setX(this.width / 2 - stringWidget.getWidth() / 2);
        stringWidget.setY(10);
        this.addRenderableWidget(stringWidget);

        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);

        addString(linearLayout, "general");

        int buttonWidth = this.width/3;
        int col1 = this.width / 4 - buttonWidth / 4;
        int col2 = col1+buttonWidth+5;
        addChild(linearLayout,"rrv.client_settings.itemview", Configs.CLIENT_SETTINGS.isShowOverlays(), OverlayDisplay.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setShowOverlays(sidePanel), col1, false, buttonWidth);

        addChild(linearLayout,"rrv.client_settings.sidepanel", Configs.CLIENT_SETTINGS.getSidePanel(), SidePanel.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setSidePanel(sidePanel), col2, true, buttonWidth);

        addString(linearLayout, "behavior");

        addChild(linearLayout,"rrv.client_settings.wrap_scrolling", Configs.CLIENT_SETTINGS.isWrapScrolling(), WrapScrolling.values(), (button, sidePanel)-> Configs.CLIENT_SETTINGS.setWrapScrolling(sidePanel), col1, true, buttonWidth);

        addString(linearLayout,"style");
        addChild(linearLayout, clientSetting("background.enabled"), clientSetting("background.disabled"), Configs.CLIENT_SETTINGS.drawBackground(), clientSetting("background"), (cycleButton, b )-> Configs.CLIENT_SETTINGS.setDrawBackground(b), col1, false, buttonWidth);
        addChild(linearLayout, clientSetting("resize_mode.wrap"), clientSetting("resize_mode.cut"), Configs.CLIENT_SETTINGS.isItemWrapMode(), clientSetting("resize_mode"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setItemWrapMode(b), col2, true, buttonWidth);
        addChild(linearLayout, clientSetting("center_search.centered"), clientSetting("center_search.with_index"), Configs.CLIENT_SETTINGS.isCenterSearch(), clientSetting("center_search"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setCenterSearch(b), col1, false, buttonWidth);
        addChild(linearLayout, clientSetting("show_buttons.show"), clientSetting("show_buttons.hide"), Configs.CLIENT_SETTINGS.isShowButtons(), clientSetting("show_buttons"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setShowButtons(b), col2, true, buttonWidth);
        addChild(linearLayout, clientSetting("right_index.right"), clientSetting("right_index.left"), Configs.CLIENT_SETTINGS.isRightIndex(), clientSetting("right_index"), (cycleButton, b) -> Configs.CLIENT_SETTINGS.setRightIndex(b), col1, true, buttonWidth);

        addString(linearLayout, "advanced");
        addChild(linearLayout, clientSetting("append_namespace.show"), clientSetting("append_namespace.hide"), Configs.CLIENT_SETTINGS.isAppendModNamespace(), clientSetting("append_namespace"),(cycleButton, b) -> Configs.CLIENT_SETTINGS.setAppendModNamespace(b), col1, false, buttonWidth);
        if (Minecraft.getInstance().level != null) {
            Button exportItemView = linearLayout.addChild(Button.builder(clientSetting("export_item_view"), ItemFilters::exportFullStackList).size(buttonWidth, 20).build());
            exportItemView.setX(col2);
            exportItemView.setY(yPos);
        }

        Button close = this.addRenderableWidget(this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).size(100, 20).build()));
        close.setX(this.width/2 - close.getWidth()/2);
        close.setY(this.height - 25);

        linearLayout.visitWidgets(this::addRenderableWidget);
    }

	private void addString(LinearLayout linearLayout, String string) {
        StringWidget behavior = new StringWidget(clientSetting(string), this.font);
        behavior.setX(this.width/2 - behavior.getWidth()/2);
        behavior.setY(yPos+3);
        yPos+=15;
        linearLayout.addChild(behavior);
	}

    public static MutableComponent clientSetting(String s) {
		return Component.translatable("rrv.client_settings." + s);
	}

    private <T extends StringRepresentable> void addChild(LinearLayout linearLayout, String key, T initialValue, T[] values, CycleButton.OnValueChange<T> newValueSetter, int x, boolean newLine, int width) {
        linearLayout.addChild(
        CycleButton.builder((value)-> Component.translatable(key+"."+value.getSerializedName()), initialValue).withValues(values)
                .create(x, yPos, width, 20, Component.translatable(key), newValueSetter)
        );
        if (newLine)
            yPos+=22;

	}

    private void addChild(LinearLayout linearLayout, MutableComponent enabled, MutableComponent disabled, boolean currentValue, MutableComponent translatable, CycleButton.OnValueChange<Boolean> newValueSetter, int x, boolean newLine, int width) {
        linearLayout.addChild(CycleButton.booleanBuilder(enabled, disabled, currentValue).create(x, yPos, width, 20, translatable, newValueSetter));
        if (newLine)
            yPos+=22;
    }

    @Override
    public void resize(int width, int height) {
        this.minecraft.setScreen(new RrvClientSettingsScreen(this.lastScreen));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Configs.CLIENT_SETTINGS.save();
        this.minecraft.setScreen(this.lastScreen);
    }
}
