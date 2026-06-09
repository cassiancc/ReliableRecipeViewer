package cc.cassian.rrv.common.network.payload.sharing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundShareRecipePayload(Identifier recipeId, String senderUuid, Component senderName) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf,  ClientboundShareRecipePayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			ClientboundShareRecipePayload::recipeId,
			ByteBufCodecs.STRING_UTF8,
			ClientboundShareRecipePayload::senderUuid,
			ComponentSerialization.STREAM_CODEC,
            ClientboundShareRecipePayload::senderName,
            ClientboundShareRecipePayload::new
	);

	public static final Type<ClientboundShareRecipePayload> TYPE = new Type<>(ReliableRecipeViewer.of("share_recipe_to_client"));


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}