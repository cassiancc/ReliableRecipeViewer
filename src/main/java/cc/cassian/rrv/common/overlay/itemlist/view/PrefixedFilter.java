package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
import cc.cassian.rrv.common.integration.ModCompat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public record PrefixedFilter(String name, Function<String, List<ItemStack>> filter, BiFunction<ItemStack, String, Boolean> advancedFilter) implements StringRepresentable {
	public static ArrayList<PrefixedFilter> FILTERS = new ArrayList<>();
	public static final PrefixedFilter NAMESPACE = new PrefixedFilter("namespace", ItemFilters::modNamespace, ItemFilters::modNamespace);
	public static final PrefixedFilter ID = new PrefixedFilter("id", ItemFilters::id, ItemFilters::id);
	public static final PrefixedFilter ITEM_TAG = new PrefixedFilter("item_tag", ItemFilters::tag, ItemFilters::tag);
	public static final PrefixedFilter DATA_COMPONENT = new PrefixedFilter("data_component", ItemFilters::component, ItemFilters::component);
	public static final PrefixedFilter CREATIVE_TAB = new PrefixedFilter("creative_tab", ItemFilters::creativeTab, ItemFilters::creativeTab);
	public static final PrefixedFilter COLOR = new PrefixedFilter("jei:color", ItemFilters::color, ItemFilters::color);

	public PrefixedFilter(String name, Function<String, List<ItemStack>> filter, BiFunction<ItemStack, String, Boolean> advancedFilter) {
		this.name = name;
		this.filter = filter;
		this.advancedFilter = advancedFilter;
		FILTERS.add(this);
	}

	public record Configuration(String prefix, TextColor color, boolean enabled) {
		public Configuration(String prefix, ChatFormatting color, boolean enabled) {
			this(prefix, TextColor.fromLegacyFormat(color), enabled);
		}

		public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
						Codec.STRING.fieldOf("prefix").forGetter(Configuration::prefix),
						TextColor.CODEC.fieldOf("color").forGetter((Configuration t) -> t.color),
						Codec.BOOL.fieldOf("enabled").forGetter(Configuration::enabled)
				)
				.apply(instance, Configuration::new));
	}

	private static final Codec<PrefixedFilter> SOURCE_CODEC = StringRepresentable.fromValues(()-> FILTERS.toArray(new PrefixedFilter[]{}));
	public static final Codec<Map<PrefixedFilter, Configuration>> CODEC = Codec.unboundedMap(PrefixedFilter.SOURCE_CODEC, PrefixedFilter.Configuration.CODEC);

	//~ if <26.2 'TextColor'->'ChatFormatting' {
	public static final Map<PrefixedFilter, Configuration> DEFAULT = Map.of(
			NAMESPACE, new Configuration("@", ChatFormatting.GOLD, true),
			ID, new Configuration(":", ChatFormatting.GREEN, true),
			ITEM_TAG, new Configuration("#", ChatFormatting.LIGHT_PURPLE, true),
			DATA_COMPONENT, new Configuration("$", ChatFormatting.GRAY, true),
			CREATIVE_TAB, new Configuration("%", ChatFormatting.BLUE, true),
			COLOR, new Configuration("^", ChatFormatting.YELLOW, ModCompat.JEI)
	);
	//~}

	public TextColor color() {
		return Configs.CLIENT_SETTINGS.getSearchFilters().get(this).color();
	}

	public String prefix() {
		return Configs.CLIENT_SETTINGS.getSearchFilters().get(this).prefix();
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public static @Nullable PrefixedFilter findFilterInQuery(String query) {
		for (PrefixedFilter value : PrefixedFilter.values()) {
			if (query.contains(value.prefix())) {
				return value;
			}
		}
		return null;
	}

	public static boolean startsWithPrefix(String query) {
		for (PrefixedFilter value : PrefixedFilter.values()) {
			if (query.startsWith(value.prefix())) {
				return true;
			}
		}
		return false;
	}

	public static List<PrefixedFilter> values() {
		return Configs.CLIENT_SETTINGS.getSearchFilters().entrySet().stream().filter(prefixedFilterBooleanEntry -> prefixedFilterBooleanEntry.getValue().enabled).map(Map.Entry::getKey).toList();
	}
}
