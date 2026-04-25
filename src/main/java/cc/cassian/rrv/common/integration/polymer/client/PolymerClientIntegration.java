//? fabric {
package cc.cassian.rrv.common.integration.polymer.client;

import cc.cassian.rrv.common.integration.polymer.PolymerIntegration;
import cc.cassian.rrv.common.integration.polymer.network.ItemStackModifierSetPayload;
import cc.cassian.rrv.common.integration.polymer.network.ItemStackRemoverSetPayload;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class PolymerClientIntegration {

    public static void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(StackActionPayload.PACKET_ID, (payload, context) -> {

        });

        ClientPlayNetworking.registerGlobalReceiver(ItemStackModifierSetPayload.PACKET_ID, (payload, context) -> {
            PolymerIntegration.ITEM_STACKS = new ArrayList<>();
            List<ItemStack> stacks = payload.itemStacks();
            if (stacks != null) {
                PolymerIntegration.ITEM_STACKS.addAll(stacks);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(ItemStackRemoverSetPayload.PACKET_ID, (payload, context) -> {
            PolymerIntegration.REMOVED_ITEM_STACKS = new ArrayList<>();
            List<ItemStack> stacks = payload.itemStacks();
            if (stacks != null) {
                PolymerIntegration.REMOVED_ITEM_STACKS.addAll(stacks);
            }
        });

    }
}
//?}