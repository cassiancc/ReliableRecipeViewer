package cc.cassian.rrv.common.network.payload.transfer;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundUpdateTransferCachePayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateTransferCachePayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundUpdateTransferCachePayload::write, ClientboundUpdateTransferCachePayload::new);
    public static final Type<ClientboundUpdateTransferCachePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "update_transfer_cache"));

    public ClientboundUpdateTransferCachePayload(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
