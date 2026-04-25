//? fabric {
package cc.cassian.rrv.common.integration.polymer;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import eu.pb4.polydex.api.v1.recipe.PolydexEntry;
import eu.pb4.polydex.api.v1.recipe.PolydexPageUtils;
import eu.pb4.polydex.impl.PolydexImpl;

import java.util.ArrayList;
import java.util.Optional;

public class PolydexIntegration {
    static void receive(StackActionPayload payload, ServerPlayNetworking.Context context) {
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