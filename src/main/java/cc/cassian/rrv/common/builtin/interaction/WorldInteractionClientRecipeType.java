package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class WorldInteractionClientRecipeType implements ReliableClientRecipeType {

	protected static final WorldInteractionClientRecipeType INSTANCE = new WorldInteractionClientRecipeType();

	private static final ResourceLocation ANVIL_LOCATION = ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/anvil.png");

	@Override
	public Component getDisplayName() {
		return Component.translatable("view.rrv.type.world_interaction");
	}

	@Override
	public ResourceLocation getId() {
		return ResourceLocation.fromNamespaceAndPath("rrv", "world_interaction");
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.GRASS_BLOCK);
	}

	@Override
	public int getDisplayWidth() {
		return 129;
	}

	@Override
	public int getDisplayHeight() {
		return 20;
	}

	@Override
	public ResourceLocation getGuiTexture() {
		return ANVIL_LOCATION;
	}

	@Override
	public int getSlotCount() {
		return 3;
	}

	@Override
	public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

		//Base
		slotDefinition.addItemSlot(0, 3, 2);

		//Repair Ingredient
		slotDefinition.addItemSlot(1, 52, 2);

		//Result
		slotDefinition.addItemSlot(2, 110, 2);
	}
}
