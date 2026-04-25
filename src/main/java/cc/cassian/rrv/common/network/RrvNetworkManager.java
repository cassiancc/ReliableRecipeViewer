package cc.cassian.rrv.common.network;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.Platform;
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
import cc.cassian.rrv.client.recipe.InternalRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.cache.LowEndRecipeCache;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
//? fabric {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Network Manager for all RRV packets
 */
@ApiStatus.Internal
public class RrvNetworkManager {

    /**
     * The NetworkManager instance of RRV
     */
    public static final RrvNetworkManager INSTANCE = new RrvNetworkManager();

    //? neoforge
    //public static PayloadRegistrar event;


    private RrvNetworkManager() {}


    public static boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        //? fabric {
        return ServerPlayNetworking.canSend(player, type);
        //?} else {
        /*var connection = player.connection;
        if (connection != null)
            return connection.hasChannel(type.id());
        return false;
        *///?}
    }

    /**
     * Registers a new serverbound packet type
     * @param type The packet type
     * @param codec The codec for the packet
     * @param serverHandler The server payload handler
     */
    public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<ServerContext, T> serverHandler) {
        //? fabric {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, ((payload, context) -> {
            serverHandler.handle(new ServerContext(context.server(), context.player()), payload);
        }));
        //?} else {
        /*event.playToServer(type, codec, (payload, context)-> {
            serverHandler.handle(new ServerContext(context.player().level().getServer(), (ServerPlayer) context.player()), payload);
        });
        *///?}


    }

    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     * @param clientHandler The client payload handler
     */
    public static <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ClientNetworkManager.PayloadHandler<ClientNetworkManager.ClientContext, T> clientHandler) {
        //? fabric {
        registerClientboundPayload(type, codec);
        if (Platform.INSTANCE.isClientSide()) {
            ClientNetworkManager.registerClientboundReciever(type, codec, clientHandler);
        }
        //?} else {
        /*event.playToClient(type, codec, (payload, context) -> {
           clientHandler.handle(new ClientNetworkManager.ClientContext(Optional.empty()), payload);
        });
        *///?}
    }


    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     */
    public static <T extends CustomPacketPayload> void registerClientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        //? fabric {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
        //?}
    }

    /**
     * Send a payload to a player
     *
     * @param player  The player
     * @param payload The payload
     */
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        //? fabric {
        ServerPlayNetworking.send(player, payload);
        //?} else {
        /*PacketDistributor.sendToPlayer(player, payload);
        *///?}
    }


    /**
     * Registers all RRV payloads
     *
     * @return The instance of the NetworkManager
     */
    public RrvNetworkManager registerPayloads(
            //? neoforge
            //RegisterPayloadHandlersEvent event
    ) {
        //? neoforge
        //RrvNetworkManager.event = event.registrar("1").optional();
        registerClientbound(ClientboundServerReloadPayload.TYPE, ClientboundServerReloadPayload.STREAM_CODEC, (context, payload) -> {
            ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
        });

        //Stack-Sensitives
        registerClientbound(ClientboundStartStackSensitivesPayload.TYPE, ClientboundStartStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveStartRecieved(payload.amount());
        });

        registerClientbound(ClientboundStackSensitivePayload.TYPE, ClientboundStackSensitivePayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveRecieved(payload.stackSensitive());
        });

        registerClientbound(ClientboundFinishStackSensitivesPayload.TYPE, ClientboundFinishStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveEndRecieved();
        });

        /*
         * Enclosing payloads (for update start and end)
         */
        registerClientbound(ClientboundStartUpdatesPayload.TYPE, ClientboundStartUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(InternalRecipeManager.INSTANCE::startUpdate);
        });

        registerClientbound(ClientboundFinishUpdatesPayload.TYPE, ClientboundFinishUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(InternalRecipeManager.INSTANCE::processRecipes);
            InternalRecipeManager.INSTANCE.runTasks();
        });

        //Recipes
        registerClientbound(ClientboundCacheStartPayload.TYPE, ClientboundCacheStartPayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheStartRecieved(payload.types()));
        });
        registerClientbound(ClientboundTypeUpdateStartPayload.TYPE, ClientboundTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.startCaching(payload.recipeType(), payload.amount()));
        });
        registerClientbound(ClientboundTypeUpdatePayload.TYPE, ClientboundTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheModRecipe(payload.entry()));
        });
        registerClientbound(ClientboundTypeUpdateEndPayload.TYPE, ClientboundTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            InternalRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.endCaching(payload.recipeType()));
        });


        registerClientbound(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC, ClientNetworkManager::handleClientboundUpdateTransferCachePayload);

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
                        Component.translatable("cheatmode.rrv.cheated", Component.literal(String.valueOf(payload.amount())).withStyle(ChatFormatting.GOLD), payload.stack().getDisplayName().copy()).withStyle(ChatFormatting.GRAY)
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
