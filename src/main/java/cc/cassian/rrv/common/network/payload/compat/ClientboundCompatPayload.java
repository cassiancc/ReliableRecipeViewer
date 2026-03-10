package cc.cassian.rrv.common.network.payload.compat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;


import java.nio.charset.StandardCharsets;

public record ClientboundCompatPayload(CompoundTag data) implements CustomPacketPayload {

    private static final int MAX_PAYLOAD_SIZE = 1048576;

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCompatPayload> STREAM_CODEC = CustomPacketPayload.codec(
            (payload, friendlyByteBuf) -> {

            }, friendlyByteBuf -> {
                if(friendlyByteBuf.readableBytes() <= 0 || friendlyByteBuf.readableBytes() > MAX_PAYLOAD_SIZE)
                    return new ClientboundCompatPayload(new CompoundTag());

                String s = friendlyByteBuf.readBytes(friendlyByteBuf.readableBytes()).toString(StandardCharsets.UTF_8);
                try {
                    return new ClientboundCompatPayload(TagParser.parseCompoundFully(s));
                } catch (CommandSyntaxException e) {
                    return new ClientboundCompatPayload(new CompoundTag());
                }
            }
    );

    public static final CustomPacketPayload.Type<ClientboundCompatPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "compat"));


    @Override
    public @NullMarked Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


