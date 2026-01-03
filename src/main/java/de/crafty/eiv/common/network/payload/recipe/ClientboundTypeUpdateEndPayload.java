package de.crafty.eiv.common.network.payload.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundTypeUpdateEndPayload(EivRecipeType<?> recipeType) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTypeUpdateEndPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.recipeType().getId().toString(),
            s -> new ClientboundTypeUpdateEndPayload(EivRecipeType.byId(Identifier.tryParse(s)))
    );

    public static final Type<ClientboundTypeUpdateEndPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "type_update_end"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
