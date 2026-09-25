package cc.cassian.rrv.common.network.payload.reload;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.instances.ServerConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundServerConfigPayload(boolean recipeSharing) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundServerConfigPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL,
			ClientboundServerConfigPayload::recipeSharing,
            ClientboundServerConfigPayload::new
	);

	public static final Type<ClientboundServerConfigPayload> TYPE = new Type<>(ReliableRecipeViewer.of("sync_server_config"));

	public ClientboundServerConfigPayload(ServerConfig serverSettings) {
		this(serverSettings.isRecipeSharing());
	}


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}