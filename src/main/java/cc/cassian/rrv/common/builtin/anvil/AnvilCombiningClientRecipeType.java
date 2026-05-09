package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class AnvilCombiningClientRecipeType implements ReliableClientRecipeType {

	protected static final AnvilCombiningClientRecipeType INSTANCE = new AnvilCombiningClientRecipeType();

	private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/anvil.png");

	@Override
	public Component getDisplayName() {
		return Component.translatable("view.rrv.type.anvil_combining");
	}

	@Override
	public Identifier getId() {
		return ReliableRecipeViewer.of("anvil_combining");
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.ANVIL);
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

	@Override
	public List<ItemStack> getCraftReferences() {
		return List.of(Items.ANVIL.getDefaultInstance(), Items.CHIPPED_ANVIL.getDefaultInstance(), Items.DAMAGED_ANVIL.getDefaultInstance());
	}

	public ButtonData placeRecipeTransferButton(int guiLeft, int guiTop) {
		return new ButtonData(guiLeft + getDisplayWidth() + 4, guiTop + getDisplayHeight() / 2 - 14, true);
	}

	public ButtonData placeRecipeShareButton(int guiLeft, int guiTop) {
		return new ButtonData(guiLeft + getDisplayWidth() + 4, guiTop + getDisplayHeight() / 2 + 1, true);
	}
}
