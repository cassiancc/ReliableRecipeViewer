package cc.cassian.rrv.common.network.payload.stack;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundStartStackSensitivesPayload(int amount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartStackSensitivesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientboundStartStackSensitivesPayload::amount,
            ClientboundStartStackSensitivesPayload::new
    );

    public static final Type<ClientboundStartStackSensitivesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "start_stack_sensitive"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
