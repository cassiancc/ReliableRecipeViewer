package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundFinishUpdatesPayload() implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundFinishUpdatesPayload> STREAM_CODEC = CustomPacketPayload.codec((var1, var2) -> {}, ClientboundFinishUpdatesPayload::new);
    public static final Type<ClientboundFinishUpdatesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "finish_updates"));

    public ClientboundFinishUpdatesPayload(RegistryFriendlyByteBuf friendlyByteBuf) {
        this();
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
