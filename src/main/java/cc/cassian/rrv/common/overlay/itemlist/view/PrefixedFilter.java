package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum PrefixedFilter {
	NAMESPACE("@", ChatFormatting.GOLD, ItemFilters::modNamespace, ItemFilters::modNamespace),
	ID(":", ChatFormatting.GREEN, ItemFilters::id, ItemFilters::id),
	ITEM_TAG("#", ChatFormatting.LIGHT_PURPLE, ItemFilters::tag, ItemFilters::tag),
	CREATIVE_TAB("%", ChatFormatting.BLUE, ItemFilters::creativeTab, ItemFilters::creativeTab);

	private final String prefix;
	private final ChatFormatting color;
	final Function<String, List<ItemStack>> filter;
	final BiFunction<ItemStack, String, Boolean> advancedFilter;

	PrefixedFilter(String prefix, ChatFormatting color, Function<String, List<ItemStack>> filter, BiFunction<ItemStack, String, Boolean> advancedFilter) {
		this.prefix = prefix;
		this.color = color;
		this.filter = filter;
		this.advancedFilter = advancedFilter;
	}

	public String prefix() {
		return prefix;
	}

	public ChatFormatting color() {
		return color;
	}

	public static @Nullable PrefixedFilter findFilterInQuery(String query) {
		for (PrefixedFilter value : PrefixedFilter.values()) {
			if (query.contains(value.prefix)) {
				return value;
			}
		}
		return null;
	}

	public static boolean startsWithPrefix(String query) {
		for (PrefixedFilter value : PrefixedFilter.values()) {
			if (query.startsWith(value.prefix)) {
				return true;
			}
		}
		return false;
	}

}
