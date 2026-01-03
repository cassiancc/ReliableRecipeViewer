package de.crafty.eiv.common.network.payload.stack;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundFinishStackSensitivesPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundFinishStackSensitivesPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundFinishStackSensitivesPayload::write, ClientboundFinishStackSensitivesPayload::new);
    public static final Type<ClientboundFinishStackSensitivesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "finish_stack_sensitive"));

    public ClientboundFinishStackSensitivesPayload(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    public ClientboundFinishStackSensitivesPayload() {
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
