package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class RecipeCategoryConfigScreen extends Screen {

    private static final Component TITLE = Component.translatable("rrv.category_settings");
    private static final Component ENABLED = Component.translatable("rrv.category_settings.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = Component.translatable("rrv.category_settings.disabled").withStyle(ChatFormatting.RED);

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public RecipeCategoryConfigScreen(Screen lastScreen) {
        super(TITLE);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {

        // setup
        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        this.addRenderableWidget(stringWidget);
        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);

        // general
        GridLayout general = createGridLayout();
        GridLayout.RowHelper helper = general.createRowHelper(3);

        helper.addChild(new StringWidget(Component.translatable("rrv.category_settings.category"), font));
        helper.addChild(new StringWidget(Component.translatable("rrv.category_settings.enabled"), font));
        helper.addChild(new StringWidget(Component.translatable("rrv.category_settings.priority"), font));

        Configs.CATEGORIES.CATEGORIES.forEach((identifier, category) -> {
            helper.addChild(new StringWidget(Component.literal(identifier.toString()), font));
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, category.enabled()).create(0, 0, (int) (this.width / 2.5), 20, Component.literal(identifier.toString()), (_, value) -> Configs.CATEGORIES.setEnabled(identifier, value));
            helper.addChild(button1);
            IntegerEditBox widget = new IntegerEditBox(font, 0, 0, 100, 20, null);
            widget.setResponder(newPriority->{
                try {
                    Configs.CATEGORIES.setPriority(identifier, Integer.valueOf(newPriority));
                } catch (NumberFormatException ignored) {}
            });
            widget.setValue(String.valueOf(category.priority()));
            helper.addChild(widget);
        });

        linearLayout.addChild(general);

        // done

        this.addRenderableWidget(this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose()).size(100, 20).build()));

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

    @Override
    public void onClose() {
        Configs.CATEGORIES.save();
        this.minecraft.setScreen(this.lastScreen);
    }
}
