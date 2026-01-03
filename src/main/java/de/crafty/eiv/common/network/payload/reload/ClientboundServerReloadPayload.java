package de.crafty.eiv.common.network.payload.reload;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundServerReloadPayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundServerReloadPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundServerReloadPayload::write, ClientboundServerReloadPayload::new);
    public static final CustomPacketPayload.Type<ClientboundServerReloadPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "server_reload"));

    private ClientboundServerReloadPayload(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
