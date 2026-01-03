package de.crafty.eiv.common.network.payload.recipe;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundStartUpdatesPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartUpdatesPayload> STREAM_CODEC = CustomPacketPayload.codec((var1, var2) -> {}, ClientboundStartUpdatesPayload::new);
    public static final Type<ClientboundStartUpdatesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "start_updates"));

    public ClientboundStartUpdatesPayload(RegistryFriendlyByteBuf friendlyByteBuf) {
        this();
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
