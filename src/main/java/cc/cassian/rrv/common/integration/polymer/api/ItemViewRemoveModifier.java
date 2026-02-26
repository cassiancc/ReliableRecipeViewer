//? fabric && <26 {
/*package cc.cassian.rrv.common.integration.polymer.api;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface ItemViewRemoveModifier {
    Event<ItemStackRemover> ITEM_STACK_REMOVER = EventFactory.createArrayBacked(
            ItemStackRemover.class,
            (listeners) -> () -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemStackRemover callback : listeners) {
                    Stream<ItemStack> itemStackStream = callback.get()
                            .stream()
                            .map(stack -> PolymerItemUtils.createItemStack(stack, PacketContext.get()));
                    stacks.addAll(itemStackStream.toList());
                }
                return stacks;
            }
    );

    @FunctionalInterface
    public interface ItemStackRemover {
        List<ItemStack> get();
    }
}
*///?}