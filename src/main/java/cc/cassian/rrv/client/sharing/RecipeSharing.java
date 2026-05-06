package cc.cassian.rrv.client.sharing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.network.payload.sharing.ServerboundShareRecipePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class RecipeSharing {
	public static void shareRecipe(ReliableClientRecipe recipe, Player player) {
		player.sendSystemMessage(getMessage(recipe, player));
	}

	private static MutableComponent getMessage(ReliableClientRecipe recipeChatEmbedding, Player player) {
		return Component.translatable("rrv.sharing.shared_by", player.getName(), getRecipeName(recipeChatEmbedding));
	}

	private static MutableComponent getRecipeName(ReliableClientRecipe recipe) {
		Identifier recipeId = recipe.entryId();
		return Component.translatable("rrv.sharing.recipe",
				recipeId).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
				.withClickEvent(new ClickEvent.Custom(ReliableRecipeViewer.of("click_recipe"), Optional.of(StringTag.valueOf(recipeId.toString()))))
				.withHoverEvent(new HoverEvent.ShowText(Component.translatable("rrv.sharing.type", recipe.getType().getDisplayName().copy(), recipe.getResults().getFirst().next().getHoverName())))
		);
	}

	public static void shareRecipe(ReliableClientRecipe currentRecipe) {
		ClientNetworkManager.sendPacketToServer(new ServerboundShareRecipePayload(currentRecipe.getId()));
	}
}
