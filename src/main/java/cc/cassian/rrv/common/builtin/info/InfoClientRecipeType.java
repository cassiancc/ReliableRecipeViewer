package cc.cassian.rrv.common.builtin.info;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class InfoClientRecipeType implements ReliableClientRecipeType {
	public static final InfoClientRecipeType INSTANCE = new InfoClientRecipeType();
	private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/info.png");

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
	public @Nullable Identifier getGuiTexture() {
		return BACKGROUND;
	}

	@Override
	public Identifier getId() {
		return ReliableRecipeViewer.of("info");
	}

	@Override
	public ItemStack getIcon() {
		return Items.BOOK.getDefaultInstance();
	}
}
