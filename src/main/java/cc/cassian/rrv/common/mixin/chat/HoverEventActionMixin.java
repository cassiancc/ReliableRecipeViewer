package cc.cassian.rrv.common.mixin.chat;

import cc.cassian.rrv.client.sharing.RecipeSharing;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//? if fabric || >26 {

@Mixin(HoverEvent.Action.class)
public enum HoverEventActionMixin {
   RRV_SHOW_RECIPE("rrv:show_recipe", true, RecipeSharing.ShowRecipe.CODEC);

    @Shadow
    HoverEventActionMixin(final String name, final boolean allowFromServer, final MapCodec<? extends HoverEvent> codec) {

    }
//?} else {
/*@Mixin(HoverEvent.class)
public interface HoverEventActionMixin {
*///?}
}
