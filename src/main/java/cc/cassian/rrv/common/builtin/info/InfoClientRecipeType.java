package cc.cassian.rrv.common.builtin.info;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class InfoClientRecipeType implements ReliableClientRecipeType {
	protected static final InfoClientRecipeType INSTANCE = new InfoClientRecipeType();

	@Override
	public Component getDisplayName() {
		return Component.translatable("view.rrv.type.info");
	}

	@Override
	public int getDisplayWidth() {
		return 120;
	}

	@Override
	public int getDisplayHeight() {
		return 120;
	}

	@Override
	public @Nullable ResourceLocation getGuiTexture() {
		return ResourceLocation.fromNamespaceAndPath("rrv","textures/gui/type/info.png");
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
		slotDefinition.addItemSlot(0, 53, 3);
	}

	@Override
	public ResourceLocation getId() {
		return ResourceLocation.fromNamespaceAndPath("rrv","info");
	}

	@Override
	public ItemStack getIcon() {
		return Items.BOOK.getDefaultInstance();
	}
}
