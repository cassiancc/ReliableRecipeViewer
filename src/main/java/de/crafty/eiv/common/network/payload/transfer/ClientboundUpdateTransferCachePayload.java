package de.crafty.eiv.common.network.payload.transfer;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundUpdateTransferCachePayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateTransferCachePayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundUpdateTransferCachePayload::write, ClientboundUpdateTransferCachePayload::new);
    public static final Type<ClientboundUpdateTransferCachePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "update_transfer_cache"));

    public ClientboundUpdateTransferCachePayload(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
