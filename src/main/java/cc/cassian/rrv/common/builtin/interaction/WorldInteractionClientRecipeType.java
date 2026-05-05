package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WorldInteractionClientRecipeType implements ReliableClientRecipeType {

	protected static final WorldInteractionClientRecipeType INSTANCE = new WorldInteractionClientRecipeType();

	private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/anvil.png");

	@Override
	public Component getDisplayName() {
		return Component.translatable("view.rrv.type.world_interaction");
	}

	@Override
	public Identifier getId() {
		return ReliableRecipeViewer.of("world_interaction");
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.GRASS_BLOCK);
	}

	@Override
	public int getDisplayWidth() {
		return 131;
	}

	@Override
	public int getDisplayHeight() {
		return 24;
	}

	@Override
	public Identifier getGuiTexture() {
		return BACKGROUND;
	}

	@Override
	public int getSlotCount() {
		return 3;
	}

	@Override
	public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

		//Base
		slotDefinition.addItemSlot(0, 4, 4);

		//Repair Ingredient
		slotDefinition.addItemSlot(1, 53, 4);

		//Result
		slotDefinition.addItemSlot(2, 111, 4);
	}
}
