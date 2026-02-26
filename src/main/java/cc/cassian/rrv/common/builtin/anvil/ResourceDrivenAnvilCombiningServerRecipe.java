package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ResourceDrivenAnvilCombiningServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ResourceDrivenAnvilCombiningServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.fromNamespaceAndPath("rrv", "resource_driven_anvil_combining"),
            () -> new ResourceDrivenAnvilCombiningServerRecipe()
    );

    public ResourceDrivenAnvilCombiningServerRecipe() {
    }

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
