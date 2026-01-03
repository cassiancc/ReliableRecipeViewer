package de.crafty.eiv.common.builtin.blasting;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.builtin.smelting.SmeltingViewType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BlastingViewType extends SmeltingViewType {

    public static final BlastingViewType INSTANCE = new BlastingViewType();

    private static final Identifier BLASTING_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/blasting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.blasting");
    }

    @Override
    public Identifier getGuiTexture() {
        return BLASTING_LOCATION;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.BLAST_FURNACE);
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(CommonEIV.MODID, "furnace_blasting");
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.BLAST_FURNACE));
    }
}
