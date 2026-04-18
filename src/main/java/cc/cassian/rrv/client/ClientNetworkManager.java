package cc.cassian.rrv.client;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate;
import cc.cassian.rrv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
//? fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Network Manager for all clientbound RRV packets
 */
@ApiStatus.Internal
public class ClientNetworkManager {

    //? neoforge
    //public static RegisterClientPayloadHandlersEvent event;

    private ClientNetworkManager() {}

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

    public static void handleClientboundUpdateTransferCachePayload(ClientNetworkManager.ClientContext context, ClientboundUpdateTransferCachePayload payload) {
        if (RRVClientUtil.currentScreen() instanceof RecipeViewScreen viewScreen) {
			viewScreen.getMenu().updateTransferCache();
            if (SidePanelOverlay.showCraftables()) {
				SidePanelOverlay.INSTANCE.updateSidePanelIndex(SidePanelOverlay.Reason.INVENTORY_CHANGE);
			}
		}
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
        //ClientNetworkManager.event = event;
    }

	public static boolean canSend(CustomPacketPayload.Type<ServerboundRequestRrvUpdate> type) {
		//? fabric {
        return ClientPlayNetworking.canSend(type);
        //?} else {
        /*var connection = Minecraft.getInstance().getConnection();
        if (connection != null)
            return connection.hasChannel(type.id());
        return false;
        *///?}
	}

    //? fabric {
    /**
     * Registers a new clientbound packet type
     *
     * @param type          The packet type
     * @param codec         The codec for the packet
     * @param clientHandler The client payload handler
     */
    public static <T extends CustomPacketPayload> void registerClientboundReciever(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ClientNetworkManager.PayloadHandler<ClientNetworkManager.ClientContext, T> clientHandler) {
        ClientPlayNetworking.registerGlobalReceiver(type, ((payload, context) -> {
            clientHandler.handle(new ClientNetworkManager.ClientContext(Optional.of(context.client())), payload);
        }));
    }
    //?}

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
