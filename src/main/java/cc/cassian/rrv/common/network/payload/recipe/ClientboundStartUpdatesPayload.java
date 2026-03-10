package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundStartUpdatesPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartUpdatesPayload> STREAM_CODEC = CustomPacketPayload.codec((var1, var2) -> {}, ClientboundStartUpdatesPayload::new);
    public static final Type<ClientboundStartUpdatesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "start_updates"));

    public ClientboundStartUpdatesPayload(RegistryFriendlyByteBuf friendlyByteBuf) {
        this();
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
