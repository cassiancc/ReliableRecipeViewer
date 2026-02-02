//? fabric {
package cc.cassian.rrv.common.integration.polymer.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public interface ItemViewModifier {
    Event<ItemViewModifier> MODIFIER = EventFactory.createArrayBacked(
            ItemViewModifier.class,
            (listeners)->() ->{
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemViewModifier callback : listeners) {
                    stacks.addAll(callback.get());
                }
                return stacks;
            }
    );

    List<ItemStack> get();
}
//?}
