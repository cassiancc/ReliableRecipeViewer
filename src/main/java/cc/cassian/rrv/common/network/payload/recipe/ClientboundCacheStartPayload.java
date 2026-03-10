package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundCacheStartPayload(int types) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCacheStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientboundCacheStartPayload::types,
            ClientboundCacheStartPayload::new
    );

    public static final Type<ClientboundCacheStartPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "cache_start"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
