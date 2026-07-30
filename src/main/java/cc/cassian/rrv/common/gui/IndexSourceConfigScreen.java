package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
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
import net.minecraft.network.chat.Style;

import java.util.HashMap;
import java.util.Map;

public class IndexSourceConfigScreen extends ClientConfigScreen {

    private static final Component TITLE = Component.translatable("rrv.client_settings.index_source");
    private static final Component ENABLED = Component.translatable("rrv.category_settings.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED = Component.translatable("rrv.category_settings.disabled").withStyle(ChatFormatting.RED);

    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public IndexSourceConfigScreen(Screen lastScreen) {
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
        GridLayout.RowHelper helper = general.createRowHelper(2);

        int column1 = (int) (this.width / 2.5);
        int column2 = 100;

        // headers
        helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatable("rrv.client_settings.index_source").withStyle(ChatFormatting.UNDERLINE), font));
        helper.addChild(new StringWidget(column2, font.lineHeight, Component.translatable("rrv.category_settings.state").withStyle(ChatFormatting.UNDERLINE), font));
        // spacers
        helper.addChild(new SpacerElement(5, 5));
        helper.addChild(new SpacerElement(5, 5));

        Map<IndexSource, Boolean> indexSource = new HashMap<>(Configs.CLIENT_SETTINGS.getIndexSource());
        indexSource.entrySet().stream().sorted((e, f)->e.getKey().getSerializedName().compareToIgnoreCase(f.getKey().getSerializedName())).forEach((entry) -> {
            IndexSource source = entry.getKey();
            Boolean b = entry.getValue();
            String id = source.getSerializedName();
            // name
            helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatableWithFallback("rrv.client_settings.index_source."+ id, id).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.translatable("rrv.client_settings.index_source."+ id + ".tooltip")))), font));
            // enable
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, b).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(id), (button, value) -> {
				indexSource.put(source, value);
                Configs.CLIENT_SETTINGS.setIndexSource(indexSource);
                System.out.println(indexSource);
			});
            helper.addChild(button1);
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
