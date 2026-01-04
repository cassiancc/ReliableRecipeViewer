package cc.cassian.rrv.common.network.payload;

import cc.cassian.rrv.common.CommonRRV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ServerboundRequestRrvUpdate() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestRrvUpdate> STREAM_CODEC = CustomPacketPayload.codec(ServerboundRequestRrvUpdate::write, ServerboundRequestRrvUpdate::new);
    public static final CustomPacketPayload.Type<ServerboundRequestRrvUpdate> TYPE = new CustomPacketPayload.Type<ServerboundRequestRrvUpdate>(Identifier.fromNamespaceAndPath(CommonRRV.MODID, "recipe_request"));

    private ServerboundRequestRrvUpdate(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf){

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
