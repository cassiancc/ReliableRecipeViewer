package cc.cassian.rrv.client.sharing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.network.payload.sharing.ServerboundShareRecipePayload;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
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
				.withHoverEvent(new ShowRecipe(recipe.getType().getDisplayName().copy(), recipe.getType().getId().getNamespace(), recipe.getResults().getFirst().next()))
		);
	}

	public static void shareRecipe(ReliableClientRecipe currentRecipe) {
		ClientNetworkManager.sendPacketToServer(new ServerboundShareRecipePayload(currentRecipe.getId()));
	}

	public record ShowRecipe(Component recipeType, String recipeTypeNamespace, ItemStack result) implements HoverEvent {
		public static final MapCodec<ShowRecipe> CODEC = RecordCodecBuilder.mapCodec(
				i -> i.group(
						ComponentSerialization.CODEC.fieldOf("recipeType").forGetter(ShowRecipe::recipeType),
						ExtraCodecs.NON_EMPTY_STRING.fieldOf("recipeTypeNamespace").forGetter(ShowRecipe::recipeTypeNamespace),
						ItemStack.CODEC.fieldOf("result").forGetter(ShowRecipe::result)
				).apply(i, ShowRecipe::new)
		);

		@Override
		public HoverEvent.Action action() {
			return HoverEvent.Action.valueOf("rrv:show_recipe");
		}

        public List<Component> getTooltipLines() {
            return List.of(
					recipeType,
					Component.literal(Platform.INSTANCE.getModNameForNamespace(recipeTypeNamespace)).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC),
					Component.empty(),
					result.getHoverName(),
					Component.literal(Platform.INSTANCE.getModNameForItem(result)).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)
			);
        }
	}
}
