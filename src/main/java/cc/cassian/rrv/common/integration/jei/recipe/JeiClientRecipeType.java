package cc.cassian.rrv.common.integration.jei.recipe;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class JeiClientRecipeType implements ReliableClientRecipeType {
	public static final JeiClientRecipeType INSTANCE = new JeiClientRecipeType();


	@Override
	public Component getDisplayName() {
		return Component.literal("JEI");
	}

	@Override
	public int getDisplayWidth() {
		return 176;
	}

	@Override
	public int getDisplayHeight() {
		return 76;
	}

	@Override
	public @Nullable Identifier getGuiTexture() {
		return null;
	}

	@Override
	public int getSlotCount() {
		return 0;
	}

	@Override
	public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

	}

	@Override
	public int getPriority() {
		return 1000;
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath("jei", "bridge");
	}

	@Override
	public ButtonData placeRecipeShareButton(RecipeViewMenu.DisplayInfo info) {
		return ButtonData.DISABLED;
	}

	@Override
	public ItemStack getIcon() {
		return Items.KNOWLEDGE_BOOK.getDefaultInstance();
	}
}
