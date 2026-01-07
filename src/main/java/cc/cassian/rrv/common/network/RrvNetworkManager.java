package cc.cassian.rrv.common.network;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate;
import cc.cassian.rrv.common.network.payload.compat.ClientboundCompatPayload;
import cc.cassian.rrv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import cc.cassian.rrv.common.network.payload.recipe.*;
import cc.cassian.rrv.common.network.payload.reload.ClientboundServerReloadPayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStackSensitivePayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import cc.cassian.rrv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import cc.cassian.rrv.common.network.payload.transfer.ServerboundTransferPayload;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.cache.LowEndRecipeCache;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Network Manager for all RRV packets
 */
public class RrvNetworkManager {

    /**
     * The NetworkManager instance of RRV
     */
    public static final RrvNetworkManager INSTANCE = new RrvNetworkManager();


    private RrvNetworkManager() {}



    /**
     * Registers a new serverbound packet type
     * @param type The packet type
     * @param codec The codec for the packet
     * @param serverHandler The server payload handler
     */
    private <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<ServerContext, T> serverHandler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, ((payload, context) -> {
            serverHandler.handle(new ServerContext(context.server(), context.player()), payload);
        }));
    }

    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     * @param clientHandler The client payload handler
     */
    private static <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, RrvClientNetworkManager.PayloadHandler<RrvClientNetworkManager.ClientContext, T> clientHandler) {
        registerClientboundPayload(type, codec);
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            RrvClientNetworkManager.registerClientboundReciever(type, codec, clientHandler);
        }
    }


    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     */
    private static <T extends CustomPacketPayload> void registerClientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    /**
     * Send a payload to a player
     *
     * @param player  The player
     * @param payload The payload
     */
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }


    /**
     * Registers all RRV payloads
     *
     * @return The instance of the NetworkManager
     */
    public RrvNetworkManager registerPayloads() {
        registerClientbound(ClientboundServerReloadPayload.TYPE, ClientboundServerReloadPayload.STREAM_CODEC, (context, payload) -> {
            ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
        });

        //Stack-Sensitives
        registerClientbound(ClientboundStartStackSensitivesPayload.TYPE, ClientboundStartStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveStartRecrrved(payload.amount());
        });

        registerClientbound(ClientboundStackSensitivePayload.TYPE, ClientboundStackSensitivePayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveRecrrved(payload.stackSensitive());
        });

        registerClientbound(ClientboundFinishStackSensitivesPayload.TYPE, ClientboundFinishStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveEndRecrrved();
        });

        /*
         * Enclosing payloads (for update start and end)
         */
        registerClientbound(ClientboundStartUpdatesPayload.TYPE, ClientboundStartUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::startUpdate);
        });

        registerClientbound(ClientboundFinishUpdatesPayload.TYPE, ClientboundFinishUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::processRecipes);
            ClientRecipeManager.INSTANCE.runTasks();
        });

        //Recipes
        registerClientbound(ClientboundCacheStartPayload.TYPE, ClientboundCacheStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheStartRecrrved(payload.types()));
        });
        registerClientbound(ClientboundTypeUpdateStartPayload.TYPE, ClientboundTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.startCaching(payload.recipeType(), payload.amount()));
        });
        registerClientbound(ClientboundTypeUpdatePayload.TYPE, ClientboundTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheModRecipe(payload.entry()));
        });
        registerClientbound(ClientboundTypeUpdateEndPayload.TYPE, ClientboundTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.endCaching(payload.recipeType()));
        });


        registerClientboundPayload(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC);

        registerClientbound(ClientboundCompatPayload.TYPE, ClientboundCompatPayload.STREAM_CODEC, RrvPayloadConverter::convertFromCompat);

        this.registerServerbound(ServerboundRequestRrvUpdate.TYPE, ServerboundRequestRrvUpdate.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.updateStackSensitives(context.sender());
            ServerRecipeManager.INSTANCE.informAboutRecipes(context.sender());
        });



        //Item-Transfer payloads
        this.registerServerbound(ServerboundTransferPayload.TYPE, ServerboundTransferPayload.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.sender(), payload.transferMap(), payload.usedPlayerSlots());
        });


        //Cheatmode
        this.registerServerbound(ServerboundPickCheatmodeItemPayload.TYPE, ServerboundPickCheatmodeItemPayload.STREAM_CODEC, (context, payload) -> {

            if (RrvUtil.hasPermission(context.sender())) {
                context.sender().sendSystemMessage(
                        Component.literal("Cheated x").withStyle(ChatFormatting.GRAY)
                                .append(
                                        Component.literal(String.valueOf(payload.amount())).withStyle(ChatFormatting.GOLD)
                                )
                                .append(" ")
                                .append(payload.stack().getDisplayName().copy())
                );

                context.sender().addItem(payload.stack().copyWithCount(payload.amount()));
                context.sender().level().playSound(
                        null,
                        context.sender().getX(),
                        context.sender().getY(),
                        context.sender().getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        ((context.sender().getRandom().nextFloat() - context.sender().getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
            } else
                context.sender().sendSystemMessage(
                        Component.translatable("cheatmode.rrv.denied").withStyle(ChatFormatting.RED)
                );

        });


        return this;
    }


    /**
     * The context where the packet is handled in (either client or server)
     */
    public interface Context {
    }

    /**
     * Network context containing relevant information for server packet handling
     *
     * @param server The server instance
     * @param sender The player who sent the packet
     */
    public record ServerContext(MinecraftServer server, ServerPlayer sender) implements Context {
    }

    /**
     * Functional interface containing the packet handling logic
     *
     * @param <S> The context (Either ClientContext or ServerContext)
     * @param <T> The payload type
     */
    public interface PayloadHandler<S extends Context, T extends CustomPacketPayload> {

        void handle(S context, T payload);
    }

}
