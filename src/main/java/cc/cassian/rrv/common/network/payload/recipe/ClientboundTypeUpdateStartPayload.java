package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundTypeUpdateStartPayload(ReliableServerRecipeType<?> recipeType, int amount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTypeUpdateStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.recipeType().getId().toString(),
            ByteBufCodecs.INT,
            ClientboundTypeUpdateStartPayload::amount,
            (s, integer) -> new ClientboundTypeUpdateStartPayload(ReliableServerRecipeType.byId(ResourceLocation.tryParse(s)), integer)
    );

    public static final Type<ClientboundTypeUpdateStartPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "type_start"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
