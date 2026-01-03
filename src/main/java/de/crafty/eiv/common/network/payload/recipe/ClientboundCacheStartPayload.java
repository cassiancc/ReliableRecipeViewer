package de.crafty.eiv.common.network.payload.recipe;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundCacheStartPayload(int types) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCacheStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientboundCacheStartPayload::types,
            ClientboundCacheStartPayload::new
    );

    public static final Type<ClientboundCacheStartPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "cache_start"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
