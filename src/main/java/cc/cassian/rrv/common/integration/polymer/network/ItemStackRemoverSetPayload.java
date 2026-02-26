//? fabric && <26 {
/*package cc.cassian.rrv.common.integration.polymer.network;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewRemoveModifier;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public record ItemStackRemoverSetPayload(List<ItemStack> itemStacks) implements CustomPacketPayload {
	public static final ResourceLocation ID = ReliableRecipeViewer.of("itemstack_remover_set");
	public static final Type<ItemStackRemoverSetPayload> PACKET_ID = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackRemoverSetPayload> CODEC = StreamCodec.ofMember(
			ItemStackRemoverSetPayload::write,
			ItemStackRemoverSetPayload::read
	);

	public static ItemStackRemoverSetPayload read(RegistryFriendlyByteBuf buf) {
		try {
			List<ItemStack> dataResult = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
			return new ItemStackRemoverSetPayload(dataResult);
		} catch (Exception e) {
			ReliableRecipeViewer.LOGGER.error("RRV/Polymer: Failed to decode ItemStackModifierSetPayload: ", e);
			return new ItemStackRemoverSetPayload(List.of());
		}
	}

	public void write(RegistryFriendlyByteBuf buf) {
		List<ItemStack> stacks = ItemViewRemoveModifier.ITEM_STACK_REMOVER.invoker().get();
		List<ItemStack> modifiedStacks = new ArrayList<>();
		PacketContext ctx = PacketContext.create(buf.registryAccess());
		for (ItemStack stack : stacks) {
			if (PolymerItemUtils.isPolymerServerItem(stack, ctx)) {
				modifiedStacks.add(PolymerItemUtils.getClientItemStack(stack, ctx));
			}
		}
		try {
			ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, modifiedStacks);
		} catch (Exception e) {
			ReliableRecipeViewer.LOGGER.error("RRV/Polymer: Failed to encode ItemStackModifierSetPayload: ", e);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return new Type<>(ID);
	}


}
*///?}