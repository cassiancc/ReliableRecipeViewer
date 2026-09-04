//? if <26 {
/*package cc.cassian.rrv.backport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.TransmuteResult;

public record ItemStackTemplate(Holder<Item> item, int count, DataComponentPatch components) {
	
	public ItemStackTemplate(Item item) {
		this(item.builtInRegistryHolder(), 1, DataComponentPatch.EMPTY);
	}

	public ItemStackTemplate(TransmuteResult result) {
		this(result.item(), result.count(), result.components());
	}

	public static ItemStackTemplate fromNonEmptyStack(ItemStack itemStack) {
		return new ItemStackTemplate(itemStack.getItemHolder(), itemStack.getCount(), itemStack.getComponentsPatch());
	}

	public static final MapCodec<ItemStackTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Item.CODEC.fieldOf("id").forGetter(ItemStackTemplate::item),
							ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(ItemStackTemplate::count),
							DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStackTemplate::components)
					)
					.apply(i, ItemStackTemplate::new)
	);
	public static final Codec<ItemStackTemplate> CODEC = Codec.withAlternative(MAP_CODEC.codec(), Item.CODEC, item -> new ItemStackTemplate((Item)item.value()));

	public ItemStack create() {
		return new ItemStack(item, count, components);
	}

	public ItemStack apply(final DataComponentPatch additionalPatch) {
		return this.apply(this.count, additionalPatch);
	}

	public ItemStack apply(final int count, final DataComponentPatch additionalPatch) {
		ItemStack result = new ItemStack(this.item, count, additionalPatch);
		result.applyComponents(this.components);
		return result;
	}

	public ItemStackTemplate withCount(int i) {
		return new ItemStackTemplate(item, i, components);
	}
}
*///?}