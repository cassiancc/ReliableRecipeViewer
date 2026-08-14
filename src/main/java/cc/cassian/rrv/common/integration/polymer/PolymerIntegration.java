//? fabric {
package cc.cassian.rrv.common.integration.polymer;

import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewRemoveModifier;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewServerModifier;
import cc.cassian.rrv.common.integration.polymer.network.*;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
//~ if >26 'ScreenHandlerUtils'->'MenuUtils' {
import eu.pb4.polymer.core.api.other.PolymerMenuUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class PolymerIntegration {

	public static List<ItemStack> ITEM_STACKS = new ArrayList<>();
	public static List<ItemStack> REMOVED_ITEM_STACKS = new ArrayList<>();

	public static void onInitialize() {

		for (Map.Entry<Fluid, Item> entry : ItemViewRecipes.INSTANCE.fluidItemMap.entrySet()) {
			RegistrySyncUtils.setServerEntry(BuiltInRegistries.ITEM, entry.getValue());
		}

		//~}
		PolymerResourcePackUtils.addModAssets("rrv");


		//~ if <26 'clientboundPlay'->'playS2C'
		PayloadTypeRegistry<RegistryFriendlyByteBuf> clientBoundPayloads = PayloadTypeRegistry.clientboundPlay();
		//~ if <26 'serverboundPlay'->'playC2S'
		PayloadTypeRegistry<RegistryFriendlyByteBuf> serverBoundPayloads = PayloadTypeRegistry.serverboundPlay();

		clientBoundPayloads.register(ItemStackRemoverSetPayload.PACKET_ID, ItemStackRemoverSetPayload.CODEC);
		serverBoundPayloads.register(ItemStackRemoverSetPayload.PACKET_ID, ItemStackRemoverSetPayload.CODEC);

		serverBoundPayloads.register(ItemStackModifierSetPayload.PACKET_ID, ItemStackModifierSetPayload.CODEC);
		clientBoundPayloads.register(ItemStackModifierSetPayload.PACKET_ID, ItemStackModifierSetPayload.CODEC);

		serverBoundPayloads.register(JoinPayload.TYPE, JoinPayload.CODEC);
		clientBoundPayloads.register(JoinPayload.TYPE, JoinPayload.CODEC);

		clientBoundPayloads.register(StackActionPayload.PACKET_ID, StackActionPayload.CODEC);
		serverBoundPayloads.register(StackActionPayload.PACKET_ID, StackActionPayload.CODEC);

		if (ModCompat.POLYDEX)
			ServerPlayNetworking.registerGlobalReceiver(StackActionPayload.PACKET_ID, PolydexIntegration::receive);


		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();

			if (RrvNetworkManager.canSend(player, JoinPayload.TYPE)) {
				RrvNetworkManager.INSTANCE.sendPacket(player, new JoinPayload());
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();

			if (RrvNetworkManager.canSend(player, ItemStackModifierSetPayload.PACKET_ID)) {
				var stacks = ItemViewServerModifier.MODIFIER.invoker().get();
				ItemStackModifierSetPayload payload = new ItemStackModifierSetPayload(stacks);
				RrvNetworkManager.INSTANCE.sendPacket(player, payload);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();

			if (RrvNetworkManager.canSend(player, ItemStackRemoverSetPayload.PACKET_ID)) {
				List<ItemStack> stacks = ItemViewRemoveModifier.ITEM_STACK_REMOVER.invoker().get();
				ItemStackRemoverSetPayload payload = new ItemStackRemoverSetPayload(stacks);
				RrvNetworkManager.INSTANCE.sendPacket(player, payload);
			}
		});
	}




}
//?}