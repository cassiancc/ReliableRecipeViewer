package de.crafty.eiv.common.network.payload.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundTypeUpdatePayload(ServerRecipeManager.ServerRecipeEntry entry) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTypeUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ServerRecipeManager.ServerRecipeEntry.STREAM_CODEC,
            ClientboundTypeUpdatePayload::entry,
            ClientboundTypeUpdatePayload::new
    );

    public static final Type<ClientboundTypeUpdatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_update"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
