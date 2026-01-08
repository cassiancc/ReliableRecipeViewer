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
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Network Manager for all clientbound RRV packets
 */
@ApiStatus.Internal
public class RrvClientNetworkManager {

    //? neoforge
    //public static RegisterClientPayloadHandlersEvent event;

    private RrvClientNetworkManager() {}

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

    public static void handleClientboundUpdateTransferCachePayload(RrvClientNetworkManager.ClientContext context, ClientboundUpdateTransferCachePayload payload) {
        if (Minecraft.getInstance().screen instanceof RecipeViewScreen viewScreen)
            viewScreen.getMenu().updateTransferCache();
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
    }

    /**
     * Network context containing relevant information for client packet handling
     *
     * @param client The client instance
     */
    public record ClientContext(Optional<Minecraft> client) implements RrvNetworkManager.Context {
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
