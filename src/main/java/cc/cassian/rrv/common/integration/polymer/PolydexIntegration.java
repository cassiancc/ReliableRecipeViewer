//? fabric {
package cc.cassian.rrv.common.integration.polymer;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewRemoveModifier;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewServerModifier;
import cc.cassian.rrv.common.integration.polymer.network.*;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;

import eu.pb4.polydex.api.v1.recipe.PolydexEntry;
import eu.pb4.polydex.api.v1.recipe.PolydexPageUtils;
import eu.pb4.polydex.impl.PolydexImpl;
import eu.pb4.polymer.core.api.other.PolymerMenuUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class PolydexIntegration {

	public static List<ItemStack> ITEM_STACKS = new ArrayList<>();
	public static List<ItemStack> REMOVED_ITEM_STACKS = new ArrayList<>();

	public static void onInitialize() {

		for (Map.Entry<Fluid, Item> entry : ItemViewRecipes.INSTANCE.fluidItemMap.entrySet()) {
			RegistrySyncUtils.setServerEntry(BuiltInRegistries.ITEM, entry.getValue());
		}

		RegistrySyncUtils.setServerEntry(BuiltInRegistries.MENU, ReliableRecipeViewer.RECIPE_VIEW_MENU);

		if (Platform.INSTANCE.isClientSide()) {
			PolymerMenuUtils.registerType(ReliableRecipeViewer.RECIPE_VIEW_MENU);
		}
		PolymerResourcePackUtils.addModAssets("rrv");


		PayloadTypeRegistry<RegistryFriendlyByteBuf> clientBoundPayloads = PayloadTypeRegistry.clientboundPlay();
		PayloadTypeRegistry<RegistryFriendlyByteBuf> serverBoundPayloads = PayloadTypeRegistry.serverboundPlay();

		clientBoundPayloads.register(ItemStackRemoverSetPayload.PACKET_ID, ItemStackRemoverSetPayload.CODEC);
		serverBoundPayloads.register(ItemStackRemoverSetPayload.PACKET_ID, ItemStackRemoverSetPayload.CODEC);

		serverBoundPayloads.register(ItemStackModifierSetPayload.PACKET_ID, ItemStackModifierSetPayload.CODEC);
		clientBoundPayloads.register(ItemStackModifierSetPayload.PACKET_ID, ItemStackModifierSetPayload.CODEC);

		serverBoundPayloads.register(JoinPayload.TYPE, JoinPayload.CODEC);
		clientBoundPayloads.register(JoinPayload.TYPE, JoinPayload.CODEC);

		clientBoundPayloads.register(StackActionPayload.PACKET_ID, StackActionPayload.CODEC);
		serverBoundPayloads.register(StackActionPayload.PACKET_ID, StackActionPayload.CODEC);

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

	private static void receive(StackActionPayload payload, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		ActionType type = payload.openType();
		String stringId = payload.id();
		Identifier id = Identifier.parse(stringId);
		Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
		if (item.isEmpty()) return;
		ItemStack itemStack = item.get().getDefaultInstance();
		PolydexEntry entry = Optional.ofNullable(PolydexImpl.getEntry(itemStack)).orElse(PolydexImpl.ITEM_ENTRIES.nonEmptyById().get(id));
		if (entry != null) {
			switch (type) {
				case INPUT -> PolydexPageUtils.openUsagesListUi(player, entry, null);
				case RESULT -> PolydexPageUtils.openRecipeListUi(player, entry, null);
				case ANY ->
						PolydexPageUtils.openCustomPageUi(player, Component.translatable("rrv.all_recipes"), new ArrayList<>(PolydexPageUtils.getAllPages()), false, null);
			}
		}
	}


}
//?}