package cc.cassian.rrv.common.network.payload.sharing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record ClientboundShareRecipePayload(Identifier recipeId, UUID sender) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf,  ClientboundShareRecipePayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			ClientboundShareRecipePayload::recipeId,
			ByteBufCodecs.STRING_UTF8,
			clientboundShareRecipePayload -> clientboundShareRecipePayload.sender().toString(),
			(id, sender) -> new ClientboundShareRecipePayload(id, UUID.fromString(sender))
	);

	public static final Type<ClientboundShareRecipePayload> TYPE = new Type<>(ReliableRecipeViewer.of("share_recipe_to_client"));


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}