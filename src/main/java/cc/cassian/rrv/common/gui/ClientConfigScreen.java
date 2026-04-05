package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.ClientConfig;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public class ClientConfigScreen extends Screen {

    private static final Component TITLE = clientSetting("title");

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public ClientConfigScreen(Screen lastScreen) {
        super(TITLE);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {

        // setup

        ClientConfig configs = Configs.CLIENT_SETTINGS;
        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        this.addRenderableWidget(stringWidget);
        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);

        // general
        GridLayout general = createGridLayout();
        GridLayout.RowHelper generalHelper = general.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("general"), this.font));

        addChild(generalHelper, "itemview", configs.isShowItemView(), new OverlayDisplay[]{OverlayDisplay.ENABLED, OverlayDisplay.DISABLED, OverlayDisplay.WHEN_SEARCHING}, (button, sidePanel)-> configs.setShowItemView(sidePanel));
        addChild(generalHelper, "show_side_panel", configs.isShowSidePanel(), OverlayDisplay.values(), (button, sidePanel)-> configs.setShowSidePanel(sidePanel));

        linearLayout.addChild(general);

        // behavior
        GridLayout behavior = createGridLayout();
        GridLayout.RowHelper behaviorHelper = behavior.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("behavior"), this.font));

        addChild(behaviorHelper, "sidepanel", configs.getSidePanel(), SidePanel.values(), (button, sidePanel)-> configs.setSidePanel(sidePanel));
        addChild(behaviorHelper, "wrap_scrolling", configs.isWrapScrolling(), WrapScrolling.values(), (button, sidePanel)-> configs.setWrapScrolling(sidePanel));

        linearLayout.addChild(behavior);

        // style
        GridLayout style = createGridLayout();
        GridLayout.RowHelper styleHelper = style.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("style"), this.font));

        addChild(styleHelper, "background", "enabled", "disabled", configs.drawBackground(), (cycleButton, b )-> configs.setDrawBackground(b));
        addChild(styleHelper, "resize_mode", "wrap", "cut", configs.isItemWrapMode(), (cycleButton, b) -> configs.setItemWrapMode(b));
        addChild(styleHelper, "center_search", "centered", "with_index", configs.isCenterSearch(), (cycleButton, b) -> configs.setCenterSearch(b));
        addChild(styleHelper, "show_buttons", "show", "hide", configs.isShowButtons(), (cycleButton, b) -> configs.setShowButtons(b));
        addChild(styleHelper, "right_index", "right", "left", configs.isRightIndex(), (cycleButton, b) -> configs.setRightIndex(b));
        addChild(styleHelper, "recipe_screen_position", "centered", "top", configs.isCenterRecipeScreen(), (cycleButton, b) -> configs.setCenterRecipeScreen(b));

        linearLayout.addChild(style);

        // advanced
        GridLayout advanced = createGridLayout();
        GridLayout.RowHelper advancedHelper = advanced.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("advanced"), this.font));

        addChild(advancedHelper, "append_namespace", "show", "hide", configs.isAppendModNamespace(), (cycleButton, b) -> configs.setAppendModNamespace(b));
        addChild(advancedHelper, "fluid_unit", "droplets", "mb", configs.isFluidUnitDroplets(), (cycleButton, b) -> configs.setFluidUnitDroplets(b));

        if (Minecraft.getInstance().level != null)
            advancedHelper.addChild(Button.builder(clientSetting("export_item_view"), ItemFilters::exportFullStackList).size((int)(this.width/2.5), 20).build());

        linearLayout.addChild(advanced);

        // done

        this.addRenderableWidget(this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).size(100, 20).build()));

        // finalize

        ScrollableLayout scrollableLayout = this.layout.addToContents(new ScrollableLayout(Minecraft.getInstance(), linearLayout, 175));
        scrollableLayout.arrangeElements();

        scrollableLayout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    private GridLayout createGridLayout() {
        var gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        return gridLayout;
    }

    public static MutableComponent clientSetting(String s) {
		return Component.translatable("rrv.client_settings.%s".formatted(s));
	}

    private <T extends StringRepresentable> void addChild(GridLayout.RowHelper linearLayout, String key, T initialValue, T[] values, CycleButton.OnValueChange<T> newValueSetter) {
        linearLayout.addChild(
            CycleButton.builder((value)-> clientSetting(key+"."+value.getSerializedName()), initialValue)
                .withValues(values)
                .create(0, 0, (int)(this.width/2.5), 20, clientSetting(key), newValueSetter)
        );
	}

    private void addChild(GridLayout.RowHelper linearLayout, String key, String enabled, String disabled, boolean currentValue, CycleButton.OnValueChange<Boolean> newValueSetter) {
        linearLayout.addChild(CycleButton.booleanBuilder(clientSetting("%s.%s".formatted(key, enabled)), clientSetting("%s.%s".formatted(key, disabled)), currentValue).create(0, 0, (int)(this.width/2.5), 20, clientSetting(key), newValueSetter));
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
