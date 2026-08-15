package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.ClientConfig;
import cc.cassian.rrv.common.config.options.*;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
//? if <26
//import cc.cassian.rrv.backport.ScrollableLayout;

public class ClientConfigScreen extends Screen {

    private static final Component TITLE = clientSetting("title");
    public static final Component ENABLED = Component.translatable("rrv.category_settings.enabled").withStyle(ChatFormatting.GREEN);
    public static final Component DISABLED = Component.translatable("rrv.category_settings.disabled").withStyle(ChatFormatting.RED);
    public static final int PADDING = 4;

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);
    public int buttonWidth;
    protected ScrollableLayout scrollableLayout;

    public ClientConfigScreen(Screen lastScreen) {
        this(TITLE, lastScreen);
    }

    public ClientConfigScreen(Component title, Screen lastScreen) {
        super(title);

        this.lastScreen = lastScreen;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        extractListBackground(graphics);
        extractListSeparators(graphics);
    }

    private static final Identifier MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");


    protected void extractListBackground(final GuiGraphicsExtractor graphics) {
        Identifier menuListBackground = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                menuListBackground,
                0,
                scrollableLayout.getY()-PADDING,
                (float)scrollableLayout.getX()+scrollableLayout.getWidth(),
                (float)(scrollableLayout.getY()+scrollableLayout.getHeight() + (int)scrollableLayout.container.scrollAmount()),
                this.width,
                scrollableLayout.getHeight()+ PADDING,
                32,
                32
        );
    }


    protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
        Identifier headerSeparator = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        Identifier footerSeparator = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        graphics.blit(RenderPipelines.GUI_TEXTURED, headerSeparator, 0, scrollableLayout.getY() - PADDING - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        graphics.blit(RenderPipelines.GUI_TEXTURED, footerSeparator, 0, scrollableLayout.getY()+ scrollableLayout.getHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
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

        addChild(generalHelper, "itemview", configs.isShowItemView(), new OverlayDisplay[]{OverlayDisplay.ENABLED, OverlayDisplay.DISABLED, OverlayDisplay.WHEN_SEARCHING}, (u, sidePanel)-> configs.setShowItemView(sidePanel));
        addChild(generalHelper, "show_side_panel", configs.isShowSidePanel(), OverlayDisplay.values(), (u, sidePanel)-> configs.setShowSidePanel(sidePanel));
        addChild(generalHelper, "client_settings_button", configs.isClientSettingsButtonEnabled(), (u, b )-> configs.setClientSettingsButton(b));
        addChild(generalHelper, "side_panel_settings_button", configs.isSidePanelSettingsButtonEnabled(), (u, b )-> configs.setSidePanelSettingsButton(b));

        linearLayout.addChild(general);

        // behavior
        GridLayout behavior = createGridLayout();
        GridLayout.RowHelper behaviorHelper = behavior.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("behavior"), this.font));

        addChild(behaviorHelper, "sidepanel", configs.getSidePanel(), SidePanel.values(), (u, sidePanel)-> configs.setSidePanel(sidePanel));
        addChild(behaviorHelper, "wrap_scrolling", configs.isWrapScrolling(), WrapScrolling.values(), (u, sidePanel)-> configs.setWrapScrolling(sidePanel));
        addChild(behaviorHelper, "recipe_book_button", "toggles_overlay", "toggles_recipe_book", configs.isRecipeBookButton(), (u, b) -> configs.setRecipeBookButton(b));
        addChild(behaviorHelper, "recipe_sharing", configs.isRecipeSharing(), (u, b) -> configs.setRecipeSharing(b));

        addChild(behaviorHelper, "stack_groups", Configs.STACK_GROUPS.areStackGroupsEnabled(), (u, b) -> {
            Configs.STACK_GROUPS.setStackGroupsEnabled(b);
            ItemFilters.clearCaches(true);
        });


        linearLayout.addChild(behavior);

        if (ModCompat.JEI) {
            // jei
            GridLayout jei = createGridLayout();
            GridLayout.RowHelper jeiHelper = jei.createRowHelper(2);
            linearLayout.addChild(new StringWidget(clientSetting("jei"), this.font));

            addChild(jeiHelper, "jei_panel", "jei", "rrv", configs.isJeiPanel(), (u, b) -> configs.setJeiPanel(b));
            addChild(jeiHelper, "jei_recipe_screen", "jei", "rrv", configs.isJeiRecipeScreen(), (u, b) -> configs.setJeiRecipeScreen(b));

            linearLayout.addChild(jei);
        }

        // style
        GridLayout style = createGridLayout();
        GridLayout.RowHelper styleHelper = style.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("style"), this.font));

        addChild(styleHelper, "theme", "recipe_book", "classic", configs.isRecipeBookTheme(), (u, b) -> configs.setRecipeBookTheme(b));
        addChild(styleHelper, "center_search", "centered", "with_index", configs.isCenterSearch(), (u, b) -> configs.setCenterSearch(b));

        addChild(styleHelper, "append_namespace", configs.showNamespaceTooltip(), NamespaceTooltip.values(), (u, sidePanel)-> configs.setNamespaceTooltip(sidePanel));
        addChild(styleHelper, "background", "enabled", "disabled", configs.drawBackground(), (u, b )-> configs.setDrawBackground(b));

        addChild(styleHelper, "show_buttons", "show", "hide", configs.isShowButtons(), (u, b) -> configs.setShowButtons(b));
        addChild(styleHelper, "show_progress_bar", "show", "hide", configs.isShowProgressBar(), (u, b) -> configs.setShowProgressBar(b));

        addChild(styleHelper, "resize_mode", "wrap", "cut", configs.isItemWrapMode(), (u, b) -> configs.setItemWrapMode(b));
        addChild(styleHelper, "right_index", "right", "left", configs.isRightIndex(), (u, b) -> configs.setRightIndex(b));

        addChild(styleHelper, "recipe_screen_position", "centered", "top", configs.isCenterRecipeScreen(), (u, b) -> configs.setCenterRecipeScreen(b));
        addChild(styleHelper, "workstation_display", configs.getWorkstationDisplay(), WorkstationDisplay.values(), (u, workstationDisplay)-> configs.setWorkstationDisplay(workstationDisplay));

        linearLayout.addChild(style);

        // advanced
        GridLayout advanced = createGridLayout();
        GridLayout.RowHelper advancedHelper = advanced.createRowHelper(2);
        linearLayout.addChild(new StringWidget(clientSetting("advanced"), this.font));

        addChild(advancedHelper, "fluid_unit", "droplets", "mb", configs.isFluidUnitDroplets(), (u, b) -> configs.setFluidUnitDroplets(b));
        addChild(advancedHelper, "show_recipe_id", "show", "hide", configs.isShowRecipeId(), (u, b) -> configs.setShowRecipeId(b));
        addChild(advancedHelper, "local_fallback", configs.localFallbackAllowed(), LocalFallback.values(), (u, b) -> configs.setLocalFallbackAllowed(b));
        Button indexSourceSettings = Button.builder(Component.translatable("rrv.client_settings.index_source"), (button) -> RRVClientUtil.setScreen(new IndexSourceConfigScreen(this))).size(buttonWidth, 20).build();
        indexSourceSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.index_source.tooltip")));
        advancedHelper.addChild(indexSourceSettings);

        Button searchFilterSettings = Button.builder(Component.translatable("rrv.client_settings.prefixed_filters"), (button) -> RRVClientUtil.setScreen(new PrefixedFilterConfigScreen(this))).size(buttonWidth, 20).build();
        searchFilterSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.prefixed_filters.tooltip")));
        advancedHelper.addChild(searchFilterSettings);

        Button recipeCategorySettings = Button.builder(Component.translatable("rrv.category_settings"), (button) -> RRVClientUtil.setScreen(new RecipeCategoryConfigScreen(this))).size(buttonWidth, 20).build();
        if (Minecraft.getInstance().level == null) {
            recipeCategorySettings.active = false;
            recipeCategorySettings.setTooltip(Tooltip.create(Component.translatable("rrv.category_settings.needs_initial_load")));
        } else {
            recipeCategorySettings.setTooltip(Tooltip.create(Component.translatable("rrv.category_settings.tooltip")));
        }
        advancedHelper.addChild(recipeCategorySettings);


        Button stackGroupSettings = Button.builder(Component.translatable("rrv.client_settings.configure_stack_groups.title"), (button) -> RRVClientUtil.setScreen(new StackGroupConfigScreen(this))).size(buttonWidth, 20).build();
        stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.tooltip")));
        if (Minecraft.getInstance().level == null) {
            stackGroupSettings.active = false;
            stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.needs_world")));
        } else if (!Configs.STACK_GROUPS.areStackGroupsEnabled()) {
            stackGroupSettings.active = false;
            stackGroupSettings.setTooltip(Tooltip.create(Component.translatable("rrv.client_settings.configure_stack_groups.disabled")));
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

        this.addRenderableWidget(this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).size(100, 20).build()));

        // finalize

        finalizeLayout(linearLayout, layout, this);
    }

    static void finalizeLayout(LinearLayout linearLayout, HeaderAndFooterLayout layout, ClientConfigScreen screen) {
        screen.addRenderableWidget(layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> screen.onClose()).size(100, 20).build()));

        screen.scrollableLayout = layout.addToContents(new ScrollableLayout(Minecraft.getInstance(), linearLayout, screen.layout.getContentHeight()) {});
        screen.scrollableLayout.arrangeElements();

        screen.scrollableLayout.visitWidgets(screen::addRenderableWidget);
        layout.arrangeElements();
    }

    GridLayout createGridLayout() {
        var gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        return gridLayout;
    }

    void addHeader(GridLayout.RowHelper helper, MutableComponent component) {
        addHeader(helper, component, font.width(component));
    }

    void addHeader(GridLayout.RowHelper helper, MutableComponent component, int width) {
        helper.addChild(new StringWidget(width, font.lineHeight, component.withStyle(ChatFormatting.UNDERLINE), font));
    }

    static void addSpacer(GridLayout.RowHelper helper, int count) {
        for (int i = 0; i < count; i++) {
            helper.addChild(new SpacerElement(5, 5));
        }
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

    private void addChild(GridLayout.RowHelper linearLayout, String key, boolean currentValue, CycleButton.OnValueChange<Boolean> newValueSetter) {
        CycleButton<Boolean> widget = CycleButton.booleanBuilder(Component.translatable("rrv.client_settings.boolean.enabled"), Component.translatable("rrv.client_settings.boolean.disabled"), currentValue).create(0, 0, buttonWidth, 20, clientSetting(key), newValueSetter);
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

	public Screen getParentScreen() {
		return lastScreen;
	}
}
