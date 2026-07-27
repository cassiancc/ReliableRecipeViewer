//? fabric {
package cc.cassian.rrv.common.integration.polymer.network;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewServerModifier;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ItemStackModifierSetPayload(List<ItemStack> itemStacks) implements CustomPacketPayload {
	public static final Identifier ID = ReliableRecipeViewer.of("itemstack_modifier_set");
	public static final Type<ItemStackModifierSetPayload> PACKET_ID = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackModifierSetPayload> CODEC = StreamCodec.ofMember(
			ItemStackModifierSetPayload::write,
			ItemStackModifierSetPayload::read
	);

	public static ItemStackModifierSetPayload read(RegistryFriendlyByteBuf buf) {
		try {
			List<ItemStack> dataResult = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
			return new ItemStackModifierSetPayload(dataResult);
		} catch (Exception e) {
			ReliableRecipeViewer.LOGGER.error("RRV/Polymer: Failed to decode ItemStackModifierSetPayload: ", e);
			return new ItemStackModifierSetPayload(List.of());
		}
	}

	public void write(RegistryFriendlyByteBuf buf) {
		List<ItemStack> stacks = ItemViewServerModifier.MODIFIER.invoker().get();
		List<ItemStack> modifiedStacks = new ArrayList<>();
		PacketContext ctx = PacketContext.get();
		for (ItemStack stack : stacks) {
			if (PolymerItemUtils.isPolymerServerItem(stack, ctx)) {
				modifiedStacks.add(PolymerItemUtils.getPolymerItemStack(stack, ctx
						//? if >26
						, buf.registryAccess()
				));
			}
		}
		try {
			ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, modifiedStacks);
		} catch (Exception e) {
			ReliableRecipeViewer.LOGGER.error("RRV/Polymer Failed to encode ItemStackModifierSetPayload: ", e);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return new Type<>(ID);
	}


}
//?}