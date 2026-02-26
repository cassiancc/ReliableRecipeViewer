package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class AnvilCombiningClientRecipeType implements ReliableClientRecipeType {

	protected static final AnvilCombiningClientRecipeType INSTANCE = new AnvilCombiningClientRecipeType();

	private static final Identifier ANVIL_LOCATION = Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/anvil.png");

	@Override
	public Component getDisplayName() {
		return Component.translatable("view.rrv.type.anvil_combining");
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath("rrv", "anvil_combining");
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.ANVIL);
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

	@Override
	public List<ItemStack> getCraftReferences() {
		return List.of(Items.ANVIL.getDefaultInstance(), Items.CHIPPED_ANVIL.getDefaultInstance(), Items.DAMAGED_ANVIL.getDefaultInstance());
	}
}
