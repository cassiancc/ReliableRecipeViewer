package cc.cassian.rrv.common.network.payload.mode;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ServerboundPickCheatmodeItemPayload(ItemStack stack, int amount) implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickCheatmodeItemPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            serverboundPickCheatmodeItemPayload -> RrvTagUtil.encodeItemStackOnClient(serverboundPickCheatmodeItemPayload.stack()),
            ByteBufCodecs.INT,
            ServerboundPickCheatmodeItemPayload::amount,
            (compoundTag, amount) -> new ServerboundPickCheatmodeItemPayload(RrvTagUtil.decodeItemStackOnServer(compoundTag), amount)
    );

    public static final Type<ServerboundPickCheatmodeItemPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonRRV.MODID, "pick_cheatmode_item"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
