package cc.cassian.rrv.common.network.payload.sharing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerboundShareRecipePayload(Identifier recipeId) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundShareRecipePayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			ServerboundShareRecipePayload::recipeId,
			ServerboundShareRecipePayload::new
	);

	public static final Type<ServerboundShareRecipePayload> TYPE = new Type<>(ReliableRecipeViewer.of("share_recipe_to_server"));


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}



}