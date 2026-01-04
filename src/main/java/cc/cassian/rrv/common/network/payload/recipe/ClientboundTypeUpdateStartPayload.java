package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundTypeUpdateStartPayload(RrvRecipeType<?> recipeType, int amount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTypeUpdateStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.recipeType().getId().toString(),
            ByteBufCodecs.INT,
            ClientboundTypeUpdateStartPayload::amount,
            (s, integer) -> new ClientboundTypeUpdateStartPayload(RrvRecipeType.byId(Identifier.tryParse(s)), integer)
    );

    public static final Type<ClientboundTypeUpdateStartPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonRRV.MODID, "type_start"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
