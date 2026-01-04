package cc.cassian.rrv.common.builtin.blasting;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BlastingClientRecipeType extends SmeltingClientRecipeType {

    public static final BlastingClientRecipeType INSTANCE = new BlastingClientRecipeType();

    private static final Identifier BLASTING_LOCATION = Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/blasting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.blasting");
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
        return Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "furnace_blasting");
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.BLAST_FURNACE));
    }
}
