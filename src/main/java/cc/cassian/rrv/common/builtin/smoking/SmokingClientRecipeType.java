package cc.cassian.rrv.common.builtin.smoking;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmokingClientRecipeType extends SmeltingClientRecipeType {

    public static final SmokingClientRecipeType INSTANCE = new SmokingClientRecipeType();

    private static final Identifier GUI_BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/smoking.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.smoking");
    }

    @Override
    public Identifier getGuiTexture() {
        return GUI_BACKGROUND;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.SMOKER);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("furnace_smoking");
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SMOKER));
    }
}
