package cc.cassian.rrv.common.network.payload;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerboundRequestRrvUpdate() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestRrvUpdate> STREAM_CODEC = CustomPacketPayload.codec(ServerboundRequestRrvUpdate::write, ServerboundRequestRrvUpdate::new);
    public static final CustomPacketPayload.Type<ServerboundRequestRrvUpdate> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_request"));

    private ServerboundRequestRrvUpdate(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf){

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
