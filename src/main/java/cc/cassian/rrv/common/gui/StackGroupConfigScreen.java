package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import cc.cassian.rrv.common.gui.widgets.StackGroupNameWidget;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.stackgroup.data.AbstractStackGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class StackGroupConfigScreen extends ClientConfigScreen {

    private static final Component TITLE = Component.translatable("rrv.client_settings.stack_groups");
    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);
    private Identifier expandedGroupId;
    private StackGroupNameWidget expandedWidget;
    private AbstractContainerWidget scrollArea;
    private double pendingScrollAmount = 0;

    public StackGroupConfigScreen(Screen lastScreen) {
        super(TITLE, lastScreen);

        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        expandedWidget = null;

        // setup
        StringWidget stringWidget = this.layout.addToHeader(new StringWidget(TITLE, this.font));
        this.addRenderableWidget(stringWidget);
        LinearLayout linearLayout = LinearLayout.vertical().spacing(2);

        // general
        GridLayout general = createGridLayout();
        GridLayout.RowHelper helper = general.createRowHelper(3);

        int column1 = (int) (this.width / 2.5);
        int column2 = 100;

        // headers
        addHeader(helper, Component.translatable("rrv.stack_group_settings.group"), column1);
        addHeader(helper, Component.translatable("rrv.stack_group_settings.state"));
        addHeader(helper, Component.translatable("rrv.category_settings.priority"));
        // spacers
        addSpacer(helper, 3);

        StackGroupManager.stackGroups.forEach((group) -> {
            ConfiguredStackGroup current = Configs.STACK_GROUPS.getOrDefault(group.getId());
            boolean expanded = group.getId().equals(expandedGroupId);

            // name
            StackGroupNameWidget nameWidget = new StackGroupNameWidget(font, column1, 20, group, expanded, () -> toggleExpand(group.getId()));
            if (expanded) expandedWidget = nameWidget;
            helper.addChild(nameWidget);
            // enable
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, current.enabled()).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(group.getId().toString()), (button, value) -> {
                ConfiguredStackGroup latest = Configs.STACK_GROUPS.getOrDefault(group.getId());
                Configs.STACK_GROUPS.set(group.getId(), new ConfiguredStackGroup(group.getId(), value, latest.priority(), latest.order()));
			});
            helper.addChild(button1);
            // priority
            IntegerEditBox priorityBox = getPriorityBox(group, column2, current);
            helper.addChild(priorityBox);

            // reserve extra row height below the expanded group so this row's controls don't get covered by its dropdown
            if (expanded) {
                int itemCount = StackGroupManager.getGroupItems(group.getId().toString()).size();
                int dropdownHeight = StackGroupNameWidget.dropdownHeightFor(itemCount, column1);
                if (dropdownHeight > 0) {
                    helper.addChild(new SpacerElement(1, dropdownHeight));
                    helper.addChild(new SpacerElement(1, dropdownHeight));
                    helper.addChild(new SpacerElement(1, dropdownHeight));
                }
            }
        });

        linearLayout.addChild(general);

        finalizeLayout(linearLayout, layout, this);
        scrollArea = null;
        scrollableLayout.visitWidgets(widget -> {
            if (widget instanceof AbstractContainerWidget containerWidget) {
                scrollArea = containerWidget;
            }
        });
        if (scrollArea != null) {
            scrollArea.setScrollAmount(pendingScrollAmount);
        }
    }

    private @NonNull IntegerEditBox getPriorityBox(AbstractStackGroup group, int column2, ConfiguredStackGroup current) {
        IntegerEditBox priorityBox = new IntegerEditBox(font, 0, 0, column2, 20, null);
        priorityBox.setResponder(newPriority->{
            try {
                int value = Integer.parseInt(newPriority);
                ConfiguredStackGroup latest = Configs.STACK_GROUPS.getOrDefault(group.getId());
                Configs.STACK_GROUPS.set(group.getId(), new ConfiguredStackGroup(group.getId(), latest.enabled(), value, latest.order()));
            } catch (NumberFormatException ignored) {}
        });
        priorityBox.setValue(String.valueOf(current.priority()));
        return priorityBox;
    }

    private void toggleExpand(Identifier groupId) {
        if (scrollArea != null) {
            pendingScrollAmount = scrollArea.scrollAmount();
        }
        expandedGroupId = groupId.equals(expandedGroupId) ? null : groupId;
        this.rebuildWidgets();
    }

    //~ if >26 'render'->'extractRenderState' {
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    //~}
        if (expandedWidget != null) {
            guiGraphics.nextStratum();
            boolean clipped = scrollArea != null;
            if (clipped) {
                guiGraphics.enableScissor(scrollArea.getX(), scrollArea.getY(), scrollArea.getX() + scrollArea.getWidth(), scrollArea.getY() + scrollArea.getHeight());
            }
            expandedWidget.renderDropdownOverlay(guiGraphics, mouseX, mouseY);
            if (clipped) {
                guiGraphics.disableScissor();
            }
        }
    }

    private void save() {
        Configs.STACK_GROUPS.save();
        StackGroupManager.reload();
        ItemFilters.clearCaches(true);
    }

    @Override
    public void onClose() {
        save();
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
