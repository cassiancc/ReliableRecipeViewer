package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.gui.list.StackGroupGridList;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class StackGroupConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("rrv.client_settings.configure_stack_groups.title");
    private final Screen lastScreen;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 36, 36);
    private final Set<String> disabledGroups = new HashSet<>();
    private StackGroupGridList list;
    private EditBox searchBox;

    public StackGroupConfigScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
        this.disabledGroups.addAll(Configs.CLIENT_SETTINGS.getDisabledStackGroups());
    }

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 14, Component.translatable("selectWorld.search"));
        this.searchBox.setResponder(text -> {
            if (this.list != null) {
                this.list.setSearchQuery(text.toLowerCase(Locale.ROOT));
                this.list.refreshList();
            }
        });
        this.addRenderableWidget(this.searchBox);

        this.list = new StackGroupGridList(this, this.disabledGroups);
        this.list.refreshList();
        this.layout.addToContents(this.list);

        StringWidget titleWidget = new StringWidget(TITLE, this.font);
        this.layout.addToHeader(titleWidget);

        Button doneButton = Button.builder(CommonComponents.GUI_DONE, _ -> {
            save();
            onClose();
        }).width(200).build();
        this.layout.addToFooter(doneButton);

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    private void save() {
        Configs.CLIENT_SETTINGS.getDisabledStackGroups().clear();
        Configs.CLIENT_SETTINGS.getDisabledStackGroups().addAll(this.disabledGroups);
        Configs.CLIENT_SETTINGS.save();
        StackGroupManager.reload();
        ItemFilters.cached = false;
    }

    protected void repositionElements() {
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
        this.layout.arrangeElements();
        if (this.searchBox != null) {
            this.searchBox.setX(this.width / 2 - 100);
            this.searchBox.setY(30);
        }
    }

    @Override
    public void onClose() {
        save();
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
