package cc.cassian.rrv.common.network.payload.reload;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundServerReloadPayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundServerReloadPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundServerReloadPayload::write, ClientboundServerReloadPayload::new);
    public static final CustomPacketPayload.Type<ClientboundServerReloadPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "server_reload"));

    private ClientboundServerReloadPayload(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
