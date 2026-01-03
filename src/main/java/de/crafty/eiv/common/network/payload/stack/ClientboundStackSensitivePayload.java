package de.crafty.eiv.common.network.payload.stack;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.payload.recipe.ClientboundFinishUpdatesPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundStackSensitivePayload(ItemView.StackSensitive stackSensitive) implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStackSensitivePayload> STREAM_CODEC = StreamCodec.composite(
            ItemView.StackSensitive.STREAM_CODEC,
            ClientboundStackSensitivePayload::stackSensitive,
            ClientboundStackSensitivePayload::new
    );

    public static final Type<ClientboundStackSensitivePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "stack_sensitive"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
