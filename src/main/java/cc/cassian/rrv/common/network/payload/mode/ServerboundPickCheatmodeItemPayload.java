package cc.cassian.rrv.common.network.payload.mode;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;


public record ServerboundPickCheatmodeItemPayload(ItemStack stack, int amount) implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickCheatmodeItemPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            serverboundPickCheatmodeItemPayload -> TagUtil.encodeItemStackOnClient(serverboundPickCheatmodeItemPayload.stack()),
            ByteBufCodecs.INT,
            ServerboundPickCheatmodeItemPayload::amount,
            (compoundTag, amount) -> new ServerboundPickCheatmodeItemPayload(TagUtil.decodeItemStackOnServer(compoundTag), amount)
    );

    public static final Type<ServerboundPickCheatmodeItemPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "pick_cheatmode_item"));


    @Override
    public @NullMarked Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
