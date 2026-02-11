package cc.cassian.rrv.common.integration.polymer.network;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class JoinPayload implements CustomPacketPayload {
	public static final Identifier ID = ReliableRecipeViewer.of("join_payload");
	public static final Type<JoinPayload> TYPE = new Type<>(ID);

	public static final StreamCodec<RegistryFriendlyByteBuf, JoinPayload> CODEC = StreamCodec.ofMember(
			JoinPayload::write,
			JoinPayload::read
	);

	private static JoinPayload read(RegistryFriendlyByteBuf buf) {
		return new JoinPayload();
	}

	private void write(RegistryFriendlyByteBuf buf) {

	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
