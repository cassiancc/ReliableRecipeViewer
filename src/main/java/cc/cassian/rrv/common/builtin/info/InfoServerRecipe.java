package cc.cassian.rrv.common.builtin.info;


import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class InfoServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<InfoServerRecipe> TYPE = ReliableServerRecipeType.register(
			ResourceLocation.fromNamespaceAndPath("rrv", "info"),
			() -> new InfoServerRecipe()
	);

	@Override
	public void writeToTag(CompoundTag tag) {

	}

	@Override
	public void loadFromTag(CompoundTag tag) {

	}

	@Override
	public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
		return TYPE;
	}
}