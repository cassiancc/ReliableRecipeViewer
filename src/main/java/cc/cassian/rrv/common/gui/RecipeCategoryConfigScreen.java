package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RecipeCategoryConfigScreen extends ClientConfigScreen {

    private static final Component TITLE = Component.translatable("rrv.category_settings");
    private static final Component ENABLED = Component.translatable("rrv.category_settings.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = Component.translatable("rrv.category_settings.disabled").withStyle(ChatFormatting.RED);

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public RecipeCategoryConfigScreen(Screen lastScreen) {
        super(TITLE, lastScreen);

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

        int column1 = (int) (this.width / 2.5);
        int column2 = 100;

        helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatable("rrv.category_settings.category").withStyle(ChatFormatting.UNDERLINE), font));
        helper.addChild(new StringWidget(column2, font.lineHeight, Component.translatable("rrv.category_settings.enabled").withStyle(ChatFormatting.UNDERLINE), font));
        helper.addChild(new StringWidget(column2, font.lineHeight, Component.translatable("rrv.category_settings.priority").withStyle(ChatFormatting.UNDERLINE), font));

        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));

        Configs.CATEGORIES.CATEGORIES.values().forEach((category) -> {
            Identifier id = category.id();
            helper.addChild(new StringWidget(column1, font.lineHeight, Component.literal(id.toString()), font));
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, category.enabled()).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(id.toString()), (_, value) -> Configs.CATEGORIES.setEnabled(id, value));
            helper.addChild(button1);
            IntegerEditBox widget = new IntegerEditBox(font, 0, 0, column2, 20, null);
            widget.setResponder(newPriority->{
                try {
                    Configs.CATEGORIES.setPriority(id, Integer.valueOf(newPriority));
                } catch (NumberFormatException ignored) {}
            });
            widget.setValue(String.valueOf(category.priority()));
            helper.addChild(widget);
        });

        linearLayout.addChild(general);

        // done

        finalizeLayout(linearLayout, layout, this);
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
