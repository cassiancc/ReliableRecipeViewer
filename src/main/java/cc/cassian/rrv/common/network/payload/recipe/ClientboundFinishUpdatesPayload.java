package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.CommonRRV;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundFinishUpdatesPayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundFinishUpdatesPayload> STREAM_CODEC = CustomPacketPayload.codec((var1, var2) -> {}, ClientboundFinishUpdatesPayload::new);
    public static final Type<ClientboundFinishUpdatesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonRRV.MODID, "finish_updates"));

    public ClientboundFinishUpdatesPayload(RegistryFriendlyByteBuf friendlyByteBuf) {
        this();
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
