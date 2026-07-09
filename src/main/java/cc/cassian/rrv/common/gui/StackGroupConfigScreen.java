package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackGroupConfigScreen extends ClientConfigScreen {

    private static final Component TITLE = Component.translatable("rrv.client_settings.stack_groups");
    private static final Component ENABLED = Component.translatable("rrv.stack_group_settings.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = Component.translatable("rrv.stack_group_settings.disabled").withStyle(ChatFormatting.RED);


    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public StackGroupConfigScreen(Screen lastScreen) {
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
        helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatable("rrv.stack_group_settings.group").withStyle(ChatFormatting.UNDERLINE), font));
        helper.addChild(new StringWidget(column2, font.lineHeight, Component.translatable("rrv.category_settings.state").withStyle(ChatFormatting.UNDERLINE), font));
        MutableComponent priorityText = Component.translatable("rrv.category_settings.priority");
        helper.addChild(new StringWidget(font.width(priorityText), font.lineHeight, priorityText.withStyle(ChatFormatting.UNDERLINE), font));
//        helper.addChild(new SpacerElement(5, 5));
        // spacers
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));
//        helper.addChild(new SpacerElement(5, 5));

        StackGroupManager.stackGroups.forEach((group) -> {
            Identifier id = group.getId();
            // name
            helper.addChild(new StringWidget(column1, font.lineHeight, group.getName().copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal(id.toString())))), font));
            // enable
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, group.isEnabled).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(id.toString()), (_, value) -> {
                Configs.STACK_GROUPS.set(group.getId(), new ConfiguredStackGroup(group.getId(), value, group.priority, List.of()));
			});
            helper.addChild(button1);
            // priority
            IntegerEditBox priorityBox = new IntegerEditBox(font, 0, 0, column2, 20, null);
            priorityBox.setResponder(newPriority->{
                try {
                    int value = Integer.parseInt(newPriority);
                    Configs.STACK_GROUPS.set(id, new ConfiguredStackGroup(group.getId(), group.isEnabled, value, List.of()));
                } catch (NumberFormatException ignored) {}
            });
            priorityBox.setValue(String.valueOf(group.priority));
            helper.addChild(priorityBox);
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

    private void save() {
        Configs.STACK_GROUPS.save();
        StackGroupManager.reload();
        ItemFilters.cached = false;
    }

    @Override
    public void onClose() {
        save();
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
