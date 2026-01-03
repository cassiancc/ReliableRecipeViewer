package de.crafty.eiv.common.network.payload.stack;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundStartStackSensitivesPayload(int amount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartStackSensitivesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientboundStartStackSensitivesPayload::amount,
            ClientboundStartStackSensitivesPayload::new
    );

    public static final Type<ClientboundStartStackSensitivesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "start_stack_sensitive"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
