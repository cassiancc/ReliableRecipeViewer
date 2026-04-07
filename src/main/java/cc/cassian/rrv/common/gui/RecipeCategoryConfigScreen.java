package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.RecipeCategoryConfig;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.Comparator;

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
        int column3 = 20;

        // headers
        helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatable("rrv.category_settings.category").withStyle(ChatFormatting.UNDERLINE), font));
        helper.addChild(new StringWidget(column2, font.lineHeight, Component.translatable("rrv.category_settings.state").withStyle(ChatFormatting.UNDERLINE), font));
        MutableComponent priorityText = Component.translatable("rrv.category_settings.priority");
        helper.addChild(new StringWidget(font.width(priorityText), font.lineHeight, priorityText.withStyle(ChatFormatting.UNDERLINE), font));
//        helper.addChild(new SpacerElement(5, 5));
        // spacers
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));
//        helper.addChild(new SpacerElement(5, 5));

        Configs.CATEGORIES.CATEGORIES.values().stream().sorted(Comparator.comparingInt(RecipeCategoryConfig.ConfiguredRecipeCategory::priority)).forEach((category) -> {
            Identifier id = category.id();
            // name
            helper.addChild(new StringWidget(column1, font.lineHeight, Component.literal(id.toString()), font));
            // enable
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, category.enabled()).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(id.toString()), (_, value) -> Configs.CATEGORIES.setEnabled(id, value));
            helper.addChild(button1);
            // priority
            IntegerEditBox priorityBox = new IntegerEditBox(font, 0, 0, column2, 20, null);
            priorityBox.setResponder(newPriority->{
                try {
                    Configs.CATEGORIES.setPriority(id, Integer.valueOf(newPriority));
                } catch (NumberFormatException ignored) {}
            });
            priorityBox.setValue(String.valueOf(category.priority()));
            helper.addChild(priorityBox);
            // priority buttons
//            helper.addChild(Button.builder(Component.literal("^"), (button)-> {
//                Configs.CATEGORIES.setPriority(id, category.priority() - 1);
//                Minecraft.getInstance().setScreen(new RecipeCategoryConfigScreen(lastScreen));
//            }).size(column3, 20).build());
//            helper.addChild(Button.builder(Component.literal("v"), (button)-> {
//                Configs.CATEGORIES.setPriority(id, category.priority() + 1);
//                Minecraft.getInstance().setScreen(new RecipeCategoryConfigScreen(lastScreen));
//            }).size(column3, 20).build());
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
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
