package cc.cassian.rrv.common.integration.polymer.recipe;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class PolydexClientRecipeType implements ReliableClientRecipeType {
	public static final PolydexClientRecipeType INSTANCE = new PolydexClientRecipeType();


	@Override
	public Component getDisplayName() {
		return Component.literal("Polydex");
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
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath("polydex", "bridge");
	}

	@Override
	public ItemStack getIcon() {
		return Items.KNOWLEDGE_BOOK.getDefaultInstance();
	}
}
