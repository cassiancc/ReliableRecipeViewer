package cc.cassian.rrv.common.integration.polymer.network;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StackActionPayload(ActionType openType, String id) implements CustomPacketPayload {
    public static final ResourceLocation ID = ReliableRecipeViewer.of("stack_action");
    public static final Type<StackActionPayload> PACKET_ID = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, StackActionPayload> CODEC = StreamCodec.ofMember(
            StackActionPayload::write,
            StackActionPayload::read
    );

    private static StackActionPayload read(RegistryFriendlyByteBuf buf) {
        try {
            return new StackActionPayload(ActionType.STREAM_CODEC.decode(buf), buf.readUtf());
        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("Can't read Stack Action Payload: ", e);
            return new StackActionPayload(ActionType.INPUT, "minecraft:stone");
        }
    }

    private void write(RegistryFriendlyByteBuf buf) {
        ActionType.STREAM_CODEC.encode(buf, this.openType);
        buf.writeUtf(this.id);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
