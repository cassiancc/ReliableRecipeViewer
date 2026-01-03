package de.crafty.eiv.common.builtin.shapeless;

import de.crafty.eiv.common.builtin.shaped.CraftingViewType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ShapelessViewType extends CraftingViewType {

    public static final ShapelessViewType INSTANCE = new ShapelessViewType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.shapeless");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("crafting_shapeless");
    }
}
