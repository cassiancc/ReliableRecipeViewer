package cc.cassian.rrv.common.network.payload.stack;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ItemView;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record ClientboundStackSensitivePayload(ItemView.StackSensitive stackSensitive) implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStackSensitivePayload> STREAM_CODEC = StreamCodec.composite(
            ItemView.StackSensitive.STREAM_CODEC,
            ClientboundStackSensitivePayload::stackSensitive,
            ClientboundStackSensitivePayload::new
    );

    public static final Type<ClientboundStackSensitivePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "stack_sensitive"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
