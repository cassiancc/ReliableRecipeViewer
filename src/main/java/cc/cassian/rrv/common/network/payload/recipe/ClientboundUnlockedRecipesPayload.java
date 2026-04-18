package cc.cassian.rrv.common.network.payload.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public record ClientboundUnlockedRecipesPayload(List<Identifier> recipes) implements CustomPacketPayload {

    public static final StreamCodec<ByteBuf, List<Identifier>> IDENTIFIER_LIST_STREAM_CODEC =
           Identifier.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUnlockedRecipesPayload> STREAM_CODEC = StreamCodec.composite(
            IDENTIFIER_LIST_STREAM_CODEC,
            ClientboundUnlockedRecipesPayload::recipes,
            ClientboundUnlockedRecipesPayload::new
    );

    public static final Type<ClientboundUnlockedRecipesPayload> TYPE = new Type<>(ReliableRecipeViewer.of("unlocked_recipes"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
