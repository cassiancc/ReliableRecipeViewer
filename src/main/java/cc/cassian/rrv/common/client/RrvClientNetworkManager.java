package cc.cassian.rrv.common.client;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
//? fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//?} else {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network Manager for all clientbound RRV packets
 */
public class RrvClientNetworkManager {

    //? neoforge
    //private static RegisterClientPayloadHandlersEvent event;

    private RrvClientNetworkManager() {}

    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     * @param clientHandler The client payload handler
     */
    public static <T extends CustomPacketPayload> void registerClientboundReciever(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, RrvClientNetworkManager.PayloadHandler<RrvClientNetworkManager.ClientContext, T> clientHandler) {
        ClientPlayNetworking.registerGlobalReceiver(type, ((payload, context) -> {
            clientHandler.handle(new RrvClientNetworkManager.ClientContext(context.client()), payload);
        }));
    }

    /**
     * Send a payload to the server
     *
     * @param payload The payload
     */
    public static void sendPacketToServer(CustomPacketPayload payload) {
        //? fabric {
        if (Minecraft.getInstance().getConnection() != null)
            ClientPlayNetworking.send(payload);
        //?} else {
        /*ClientPacketDistributor.sendToServer(payload);
        *///?}
    }


    /**
     * Registers all RRV payloads
     *
     * @return The instance of the NetworkManager
     */
    public static void registerPayloads(
            //? neoforge
            //RegisterClientPayloadHandlersEvent event
    ) {

        //? neoforge
        //RrvClientNetworkManager.event = event;

        registerClientboundReciever(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC, (context, payload) -> {
            if (context.client.screen instanceof RecipeViewScreen viewScreen)
                viewScreen.getMenu().updateTransferCache();
        });
    }

    /**
     * Network context containing relevant information for client packet handling
     *
     * @param client The client instance
     */
    public record ClientContext(Minecraft client) implements RrvNetworkManager.Context {
    }

    /**
     * Functional interface containing the packet handling logic
     *
     * @param <S> The context (Either ClientContext or ServerContext)
     * @param <T> The payload type
     */
    public interface PayloadHandler<S extends RrvNetworkManager.Context, T extends CustomPacketPayload> {

        void handle(S context, T payload);
    }

}
