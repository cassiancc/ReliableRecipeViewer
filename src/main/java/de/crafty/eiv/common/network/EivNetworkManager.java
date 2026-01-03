package de.crafty.eiv.common.network;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.payload.ServerboundRequestEivUpdate;
import de.crafty.eiv.common.network.payload.compat.ClientboundCompatPayload;
import de.crafty.eiv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import de.crafty.eiv.common.network.payload.recipe.*;
import de.crafty.eiv.common.network.payload.reload.ClientboundServerReloadPayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStackSensitivePayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import de.crafty.eiv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import de.crafty.eiv.common.network.payload.transfer.ServerboundTransferPayload;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.cache.LowEndRecipeCache;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;

/**
 * Network Manager for all EIV packets
 */
public class EivNetworkManager {

    /**
     * The NetworkManager instance of EIV
     */
    public static final EivNetworkManager INSTANCE = new EivNetworkManager().registerPayloads();

    private final HashMap<Identifier, CustomPacketPayload.TypeAndCodec<?, ?>> clientbound;
    private final HashMap<Identifier, CustomPacketPayload.TypeAndCodec<?, ?>> serverbound;

    /**
     * Payload handlers are used for packet processing on the client and server side
     */
    private final HashMap<Identifier, PayloadHandler<ClientContext, ? extends CustomPacketPayload>> clientPayloadHandlers;
    private final HashMap<Identifier, PayloadHandler<ServerContext, ? extends CustomPacketPayload>> serverPayloadHandlers;

    private EivNetworkManager() {
        this.clientbound = new HashMap<>();
        this.serverbound = new HashMap<>();

        this.clientPayloadHandlers = new HashMap<>();
        this.serverPayloadHandlers = new HashMap<>();
    }

    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     * @param clientHandler The client payload handler
     */
    private <B extends FriendlyByteBuf, T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec, PayloadHandler<ClientContext, T> clientHandler) {
        this.clientbound.put(type.id(), new CustomPacketPayload.TypeAndCodec<>(type, codec));
        this.clientPayloadHandlers.put(type.id(), clientHandler);
    }

    /**
     * Registers a new serverbound packet type
     * @param type The packet type
     * @param codec The codec for the packet
     * @param serverHandler The server payload handler
     */
    private <B extends FriendlyByteBuf, T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec, PayloadHandler<ServerContext, T> serverHandler) {
        this.serverbound.put(type.id(), new CustomPacketPayload.TypeAndCodec<>(type, codec));
        this.serverPayloadHandlers.put(type.id(), serverHandler);
    }


    /**
     * @return ALl clientbound packets
     */
    public HashMap<Identifier, CustomPacketPayload.TypeAndCodec<?, ?>> getClientbound() {
        return this.clientbound;
    }

    /**
     * @return All serverbound packets
     */
    public HashMap<Identifier, CustomPacketPayload.TypeAndCodec<?, ?>> getServerbound() {
        return this.serverbound;
    }

    /**
     * Returns all registered payload handlers for clientbound packets
     *
     * @return
     */
    public HashMap<Identifier, PayloadHandler<ClientContext, ? extends CustomPacketPayload>> clientPayloadHandlers() {
        return this.clientPayloadHandlers;
    }

    /**
     * Returns all registered payload handlers for serverbound packets
     *
     * @return
     */
    public HashMap<Identifier, PayloadHandler<ServerContext, ? extends CustomPacketPayload>> serverPayloadHandlers() {
        return this.serverPayloadHandlers;
    }


    //:D
    public <T extends CustomPacketPayload> T castPayload(CustomPacketPayload payload) {
        return (T) payload;
    }

    /**
     * Send a payload to the server
     *
     * @param payload The payload
     */
    public void sendPacketToServer(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null)
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
    }

    /**
     * Send a payload to a player
     *
     * @param player  The player
     * @param payload The payload
     */
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }


    /**
     * Registers all EIV payloads
     *
     * @return The instance of the NetworkManager
     */
    public EivNetworkManager registerPayloads() {

        this.registerServerbound(ServerboundRequestEivUpdate.TYPE, ServerboundRequestEivUpdate.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.updateStackSensitives(context.sender());
            ServerRecipeManager.INSTANCE.informAboutRecipes(context.sender());
        });

        this.registerClientbound(ClientboundServerReloadPayload.TYPE, ClientboundServerReloadPayload.STREAM_CODEC, (context, payload) -> {
            ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
        });

        //Stack-Sensitives
        this.registerClientbound(ClientboundStartStackSensitivesPayload.TYPE, ClientboundStartStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveStartReceived(payload.amount());
        });

        this.registerClientbound(ClientboundStackSensitivePayload.TYPE, ClientboundStackSensitivePayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveReceived(payload.stackSensitive());
        });

        this.registerClientbound(ClientboundFinishStackSensitivesPayload.TYPE, ClientboundFinishStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveEndReceived();
        });

        /*
         * Enclosing payloads (for update start and end)
         */
        this.registerClientbound(ClientboundStartUpdatesPayload.TYPE, ClientboundStartUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::startUpdate);
        });

        this.registerClientbound(ClientboundFinishUpdatesPayload.TYPE, ClientboundFinishUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::processRecipes);
            ClientRecipeManager.INSTANCE.runTasks();
        });

        //Recipes
        this.registerClientbound(ClientboundCacheStartPayload.TYPE, ClientboundCacheStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheStartReceived(payload.types()));
        });
        this.registerClientbound(ClientboundTypeUpdateStartPayload.TYPE, ClientboundTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.startCaching(payload.recipeType(), payload.amount()));
        });
        this.registerClientbound(ClientboundTypeUpdatePayload.TYPE, ClientboundTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheModRecipe(payload.entry()));
        });
        this.registerClientbound(ClientboundTypeUpdateEndPayload.TYPE, ClientboundTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.endCaching(payload.recipeType()));
        });


        //Item-Transfer payloads
        this.registerServerbound(ServerboundTransferPayload.TYPE, ServerboundTransferPayload.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.sender(), payload.transferMap(), payload.usedPlayerSlots());
        });
        this.registerClientbound(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC, (context, payload) -> {
            if (context.client.screen instanceof RecipeViewScreen viewScreen)
                viewScreen.getMenu().updateTransferCache();
        });


        //Cheatmode
        this.registerServerbound(ServerboundPickCheatmodeItemPayload.TYPE, ServerboundPickCheatmodeItemPayload.STREAM_CODEC, (context, payload) -> {

            if (context.sender().permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
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
                        Component.translatable("cheatmode.eiv.denied").withStyle(ChatFormatting.RED)
                );

        });

        this.registerClientbound(ClientboundCompatPayload.TYPE, ClientboundCompatPayload.STREAM_CODEC, EivPayloadConverter::convertFromCompat);

        return this;
    }


    /**
     * The context where the packet is handled in (either client or server)
     */
    public interface Context {
    }

    /**
     * Network context containing relevant information for client packet handling
     *
     * @param client The client instance
     */
    public record ClientContext(Minecraft client) implements Context {
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
