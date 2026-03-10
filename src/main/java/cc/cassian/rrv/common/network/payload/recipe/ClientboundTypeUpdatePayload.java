package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundTypeUpdatePayload(ServerRecipeManager.ServerRecipeEntry entry) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTypeUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ServerRecipeManager.ServerRecipeEntry.STREAM_CODEC,
            ClientboundTypeUpdatePayload::entry,
            ClientboundTypeUpdatePayload::new
    );

    public static final Type<ClientboundTypeUpdatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_update"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
