package cc.cassian.rrv.common.builtin;

import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class SynchronizedServerRecipeStub implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<SynchronizedServerRecipeStub> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath("rrv", "resource_driven_anvil_combining"),
            () -> new SynchronizedServerRecipeStub()
    );

    public SynchronizedServerRecipeStub() {
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
