package cc.cassian.rrv.common.gui;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
import cc.cassian.rrv.common.config.widgets.ColorEditBox;
import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import cc.cassian.rrv.common.overlay.itemlist.view.PrefixedFilter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PrefixedFilterConfigScreen extends ClientConfigScreen {

    private static final Component TITLE = Component.translatable("rrv.client_settings.prefixed_filters");
    private final Screen lastScreen;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    public PrefixedFilterConfigScreen(Screen lastScreen) {
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
        GridLayout.RowHelper helper = general.createRowHelper(4);

        int column1 = (int) (this.width / 3);
        int column2 = 50;
        int column3 = 90;

        // headers
        addHeader(helper, Component.translatable("rrv.client_settings.prefixed_filters"), column1);
        addHeader(helper, Component.translatable("rrv.category_settings.state"));
        addHeader(helper, Component.translatable("rrv.client_settings.prefixed_filters.prefix"));
        addHeader(helper, Component.translatable("rrv.client_settings.prefixed_filters.color"));
        // spacers
        addSpacer(helper, 4);

        Map<PrefixedFilter, PrefixedFilter.Configuration> prefixedFilters = new HashMap<>(Configs.CLIENT_SETTINGS.getSearchFilters());
        prefixedFilters.entrySet().stream().filter(p-> {
            String name = p.getKey().name();
            if (name.contains(":")) {
                return RRVPlatform.INSTANCE.isModLoaded(Identifier.parse(name).getNamespace());
            }
            return true;
        }).sorted((e, f)->e.getKey().name().compareToIgnoreCase(f.getKey().name())).forEach((entry) -> {
            PrefixedFilter source = entry.getKey();
            boolean b = entry.getValue().enabled();
            String prefix = entry.getValue().prefix();
            String color = entry.getValue().color().serialize();
            String id = source.name();
            // name
            helper.addChild(new StringWidget(column1, font.lineHeight, Component.translatable("rrv.client_settings.prefixed_filters."+ id.replace(":", ".")).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.translatable("rrv.client_settings.prefixed_filters."+ id.replace(":", ".") + ".tooltip")))), font));
            // enable
            CycleButton<Boolean> button1 = CycleButton.booleanBuilder(ENABLED, DISABLED, b).displayState(CycleButton.DisplayState.VALUE).create(0, 0, column2, 20, Component.literal(id), (button, value) -> {
				prefixedFilters.compute(source, (k, value1) -> new PrefixedFilter.Configuration(value1.prefix(), value1.color(), value));
                Configs.CLIENT_SETTINGS.setSearchFilters(prefixedFilters);
			});
            helper.addChild(button1);
            // prefix
            EditBox prefixBox = new EditBox(font, 0, 0, column2, 20, null);
            prefixBox.setResponder(newPrefix->{
                prefixedFilters.compute(source, (k, currentConfig) -> new PrefixedFilter.Configuration(newPrefix, currentConfig.color(), currentConfig.enabled()));
                Configs.CLIENT_SETTINGS.setSearchFilters(prefixedFilters);
            });
            prefixBox.setValue(prefix);
            helper.addChild(prefixBox);
            // prefix
            ColorEditBox colorBox = new ColorEditBox(font, 0, 0, column3, 20, null);
            colorBox.setResponder(newColor->{
                if (TextColor.parseColor(newColor).isSuccess()) {
                    prefixedFilters.compute(source, (k, currentConfig) -> new PrefixedFilter.Configuration(currentConfig.prefix(), TextColor.parseColor(newColor).getOrThrow(), currentConfig.enabled()));
                    Configs.CLIENT_SETTINGS.setSearchFilters(prefixedFilters);
                }
            });
            colorBox.setValue(color);
            helper.addChild(colorBox);
        });

        linearLayout.addChild(general);

        // done

        finalizeLayout(linearLayout, layout, this);
    }

    @Override
    public void onClose() {
        Configs.CLIENT_SETTINGS.save();
        RRVClientUtil.setScreen(this.lastScreen);
    }
}
