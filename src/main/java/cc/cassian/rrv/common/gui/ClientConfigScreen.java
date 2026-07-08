package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.ClientConfig;
import cc.cassian.rrv.common.config.options.*;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
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
    public int buttonWidth;

    public ClientConfigScreen(Screen lastScreen) {
        this(TITLE, lastScreen);
    }

    public ClientConfigScreen(Component title, Screen lastScreen) {
        super(title);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {

        // setup

        ClientConfig configs = Configs.CLIENT_SETTINGS;
        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        this.addRenderableWidget(stringWidget);
        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);
        this.buttonWidth = (int) (this.width / 2.5);

        // general
        GridLayout general = createGridLayout();
        GridLayout.RowHelper generalHelper = general.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("general"), this.font));

        addChild(generalHelper, "itemview", configs.isShowItemView(), new OverlayDisplay[]{OverlayDisplay.ENABLED, OverlayDisplay.DISABLED, OverlayDisplay.WHEN_SEARCHING}, (_, sidePanel)-> configs.setShowItemView(sidePanel));
        addChild(generalHelper, "show_side_panel", configs.isShowSidePanel(), OverlayDisplay.values(), (_, sidePanel)-> configs.setShowSidePanel(sidePanel));

        linearLayout.addChild(general);

        // behavior
        GridLayout behavior = createGridLayout();
        GridLayout.RowHelper behaviorHelper = behavior.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("behavior"), this.font));

        addChild(behaviorHelper, "sidepanel", configs.getSidePanel(), SidePanel.values(), (_, sidePanel)-> configs.setSidePanel(sidePanel));
        addChild(behaviorHelper, "wrap_scrolling", configs.isWrapScrolling(), WrapScrolling.values(), (_, sidePanel)-> configs.setWrapScrolling(sidePanel));
        addChild(behaviorHelper, "recipe_book_button", "toggles_overlay", "toggles_recipe_book", configs.isRecipeBookButton(), (_, b) -> configs.setRecipeBookButton(b));
        addChild(behaviorHelper, "recipe_sharing", "enabled", "disabled", configs.isRecipeSharing(), (_, b) -> configs.setRecipeSharing(b));
        addChild(behaviorHelper, "stack_groups", "enabled", "disabled", Configs.STACK_GROUPS.areStackGroupsEnabled(), (_, b) -> {
            Configs.STACK_GROUPS.setStackGroupsEnabled(b);
            ItemFilters.cached = false;
        });

        linearLayout.addChild(behavior);

        // style
        GridLayout style = createGridLayout();
        GridLayout.RowHelper styleHelper = style.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("style"), this.font));

        addChild(styleHelper, "theme", "recipe_book", "classic", configs.isRecipeBookTheme(), (_, b) -> configs.setRecipeBookTheme(b));
        addChild(styleHelper, "center_search", "centered", "with_index", configs.isCenterSearch(), (_, b) -> configs.setCenterSearch(b));

        addChild(styleHelper, "append_namespace", configs.showNamespaceTooltip(), NamespaceTooltip.values(), (_, sidePanel)-> configs.setNamespaceTooltip(sidePanel));
        addChild(styleHelper, "background", "enabled", "disabled", configs.drawBackground(), (_, b )-> configs.setDrawBackground(b));

        addChild(styleHelper, "show_buttons", "show", "hide", configs.isShowButtons(), (_, b) -> configs.setShowButtons(b));
        addChild(styleHelper, "show_progress_bar", "show", "hide", configs.isShowProgressBar(), (_, b) -> configs.setShowProgressBar(b));

        addChild(styleHelper, "resize_mode", "wrap", "cut", configs.isItemWrapMode(), (_, b) -> configs.setItemWrapMode(b));
        addChild(styleHelper, "right_index", "right", "left", configs.isRightIndex(), (_, b) -> configs.setRightIndex(b));

        addChild(styleHelper, "recipe_screen_position", "centered", "top", configs.isCenterRecipeScreen(), (_, b) -> configs.setCenterRecipeScreen(b));
        addChild(styleHelper, "workstation_display", configs.getWorkstationDisplay(), WorkstationDisplay.values(), (_, workstationDisplay)-> configs.setWorkstationDisplay(workstationDisplay));

        linearLayout.addChild(style);

        // advanced
        GridLayout advanced = createGridLayout();
        GridLayout.RowHelper advancedHelper = advanced.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("advanced"), this.font));

        addChild(advancedHelper, "fluid_unit", "droplets", "mb", configs.isFluidUnitDroplets(), (_, b) -> configs.setFluidUnitDroplets(b));
        addChild(advancedHelper, "show_recipe_id", "show", "hide", configs.isShowRecipeId(), (_, b) -> configs.setShowRecipeId(b));
        addChild(advancedHelper, "local_fallback", configs.localFallbackAllowed(), LocalFallback.values(), (_, b) -> configs.setLocalFallbackAllowed(b));
        addChild(advancedHelper, "index_source", configs.getIndexSource(), IndexSource.values(), (_, b) -> configs.setIndexSource(b));

        Button recipeCategorySettings = Button.builder(Component.translatable("rrv.category_settings"), (_) -> RRVClientUtil.setScreen(new RecipeCategoryConfigScreen(this))).size(buttonWidth, 20).build();
        if (Minecraft.getInstance().level == null) {
            recipeCategorySettings.active = false;
            recipeCategorySettings.setTooltip(Tooltip.create(Component.translatable("rrv.category_settings.needs_initial_load")));
        } else {
            recipeCategorySettings.setTooltip(Tooltip.create(Component.translatable("rrv.category_settings.tooltip")));
        }
        advancedHelper.addChild(recipeCategorySettings);

        Button stackGroupSettings = Button.builder(Component.translatable("rrv.client_settings.configure_stack_groups.title"), (_) -> RRVClientUtil.setScreen(new StackGroupConfigScreen(this))).size(buttonWidth, 20).build();
        stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.tooltip")));
        if (Minecraft.getInstance().level == null) {
            stackGroupSettings.active = false;
            stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.needs_world")));
        } else {
            stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.tooltip")));
        }
        advancedHelper.addChild(stackGroupSettings);

        Button exportItemViewButton = Button.builder(clientSetting("export_item_view"), ItemFilters::exportFullStackList).size(buttonWidth, 20).build();
        if (Minecraft.getInstance().level == null) {
            exportItemViewButton.active = false;
            exportItemViewButton.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.export_item_view.needs_world")));
        } else {
            exportItemViewButton.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.export_item_view.tooltip")));
        }
        advancedHelper.addChild(exportItemViewButton);

        linearLayout.addChild(advanced);

        // done

        this.addRenderableWidget(this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose()).size(100, 20).build()));

        // finalize

        finalizeLayout(linearLayout, layout, this);
    }

    static void finalizeLayout(LinearLayout linearLayout, HeaderAndFooterLayout layout, ClientConfigScreen screen) {
        screen.addRenderableWidget(layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> screen.onClose()).size(100, 20).build()));

        ScrollableLayout scrollableLayout = layout.addToContents(new ScrollableLayout(Minecraft.getInstance(), linearLayout, screen.layout.getContentHeight()));
        scrollableLayout.arrangeElements();

        scrollableLayout.visitWidgets(screen::addRenderableWidget);
        layout.arrangeElements();
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
        CycleButton<T> widget = CycleButton.builder((value) -> clientSetting(key + "." + value.getSerializedName()), initialValue)
                .withValues(values)
                .create(0, 0, buttonWidth, 20, clientSetting(key), newValueSetter);
        addTooltip(key, widget);
        linearLayout.addChild(
                widget
        );
	}

    private void addChild(GridLayout.RowHelper linearLayout, String key, String enabled, String disabled, boolean currentValue, CycleButton.OnValueChange<Boolean> newValueSetter) {
        CycleButton<Boolean> widget = CycleButton.booleanBuilder(clientSetting("%s.%s".formatted(key, enabled)), clientSetting("%s.%s".formatted(key, disabled)), currentValue).create(0, 0, buttonWidth, 20, clientSetting(key), newValueSetter);
        addTooltip(key, widget);
        linearLayout.addChild(widget);
    }

    private static void addTooltip(String key, CycleButton<?> widget) {
        var tooltipKey = "rrv.client_settings.%s.tooltip".formatted(key);
        widget.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
    }

    @Override
    public void onClose() {
        Configs.CLIENT_SETTINGS.save();
        Configs.STACK_GROUPS.save();
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
