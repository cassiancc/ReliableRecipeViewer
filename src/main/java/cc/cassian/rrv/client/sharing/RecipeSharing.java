package cc.cassian.rrv.client.sharing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class RecipeSharing {
	public static void shareRecipe(ReliableClientRecipe recipe) {
		LocalPlayer player = Minecraft.getInstance().player;
		player.sendSystemMessage(getMessage(recipe, player));
	}

	private static MutableComponent getMessage(ReliableClientRecipe recipeChatEmbedding, Player player) {
		return Component.translatable("rrv.embedding.shared_by", player.getName(), getRecipeName(recipeChatEmbedding));
	}

	private static MutableComponent getRecipeName(ReliableClientRecipe recipe) {
		return Component.translatable("rrv.embedding.recipe", recipe.entryId()).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.Custom(ReliableRecipeViewer.of("click_recipe"), Optional.of(StringTag.valueOf(recipe.entryId().toString())))));
	}
}
