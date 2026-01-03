package de.crafty.eiv.common.network.payload;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ServerboundRequestEivUpdate() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestEivUpdate> STREAM_CODEC = CustomPacketPayload.codec(ServerboundRequestEivUpdate::write, ServerboundRequestEivUpdate::new);
    public static final CustomPacketPayload.Type<ServerboundRequestEivUpdate> TYPE = new CustomPacketPayload.Type<ServerboundRequestEivUpdate>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_request"));

    private ServerboundRequestEivUpdate(FriendlyByteBuf friendlyByteBuf) {
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf){

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
