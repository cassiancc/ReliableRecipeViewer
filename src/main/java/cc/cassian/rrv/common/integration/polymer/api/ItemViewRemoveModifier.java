//? fabric {
package cc.cassian.rrv.common.integration.polymer.api;

import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.item.ItemStack;

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
                            .map(stack -> PolymerItemUtils.createItemStack(stack, PacketContext.get(), ServerRecipeManager.INSTANCE.getServer().registryAccess()));
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
//?}