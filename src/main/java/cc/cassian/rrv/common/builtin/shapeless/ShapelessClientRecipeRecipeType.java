package cc.cassian.rrv.common.builtin.shapeless;

import cc.cassian.rrv.common.builtin.shaped.CraftingClientRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ShapelessClientRecipeRecipeType extends CraftingClientRecipeType {

    public static final ShapelessClientRecipeRecipeType INSTANCE = new ShapelessClientRecipeRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.shapeless");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("crafting_shapeless");
    }
}
