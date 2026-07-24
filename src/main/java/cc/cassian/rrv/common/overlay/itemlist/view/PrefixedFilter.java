package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.common.integration.ModCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public record PrefixedFilter(String prefix, ChatFormatting color, Function<String, List<ItemStack>> filter, BiFunction<ItemStack, String, Boolean> advancedFilter, boolean enabled) {
	public static final PrefixedFilter NAMESPACE = new PrefixedFilter("@", ChatFormatting.GOLD, ItemFilters::modNamespace, ItemFilters::modNamespace, true);
	public static final PrefixedFilter ID = new PrefixedFilter(":", ChatFormatting.GREEN, ItemFilters::id, ItemFilters::id, true);
	public static final PrefixedFilter ITEM_TAG = new PrefixedFilter("#", ChatFormatting.LIGHT_PURPLE, ItemFilters::tag, ItemFilters::tag, true);
	public static final PrefixedFilter CREATIVE_TAB = new PrefixedFilter("%", ChatFormatting.BLUE, ItemFilters::creativeTab, ItemFilters::creativeTab, true);
	public static final PrefixedFilter COLOR = new PrefixedFilter("^", ChatFormatting.YELLOW, ItemFilters::color, ItemFilters::color, ModCompat.JEI);

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

	public static List<PrefixedFilter> values() {
		return Stream.of(NAMESPACE, ID, ITEM_TAG, CREATIVE_TAB, COLOR).filter(p->p.enabled).toList();
	}

}
