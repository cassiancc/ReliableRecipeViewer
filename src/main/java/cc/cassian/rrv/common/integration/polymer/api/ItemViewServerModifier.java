//? fabric {
package cc.cassian.rrv.common.integration.polymer.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public interface ItemViewServerModifier {
    Event<ItemViewServerModifier> MODIFIER = EventFactory.createArrayBacked(
            ItemViewServerModifier.class,
            (listeners) -> () -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemViewServerModifier callback : listeners) {
                    stacks.addAll(callback.get());
                }
                return stacks;
            }
    );

    List<ItemStack> get();
}
//?}