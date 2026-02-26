package cc.cassian.rrv.common.integration.polymer;

import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import cc.cassian.rrv.common.integration.polymer.recipe.PolydexClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
//? fabric && <26.1 {
/*import cc.cassian.rrv.common.integration.polymer.api.ItemViewModifier;
import cc.cassian.rrv.common.integration.polymer.api.ItemViewRemoveModifier;
import eu.pb4.polymer.core.api.client.ClientPolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.sgui.virtual.FakeScreenHandler;
import eu.pb4.sgui.virtual.VirtualScreenHandlerInterface;
*///?}

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PolymerHelpers {

	public static void polymerFilter(List<ItemStack> list) {
		//? fabric && <26.1 {
		/*Iterator<ClientPolymerItem> iterator = ClientPolymerItem.REGISTRY.stream().iterator();
		Map<ItemStack, ClientPolymerItem> registry = new Object2ObjectOpenHashMap<>();
		while (iterator.hasNext()) {
			ClientPolymerItem next = iterator.next();
			ItemStack itemStack = next.visualStack();
			if (!ClientPolymerItemUtils.isPolyItem(itemStack)) {
				continue;
			}
			Optional<String> polymerStackId = ClientPolymerItemUtils.getPolymerStackId(itemStack);
			if (polymerStackId.isEmpty()) {
				continue;
			}
			if (BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(polymerStackId.get())).isPresent()) {
				continue;
			}
			registry.put(itemStack, next);
			list.add(itemStack);
		}
		List<ItemStack> modifierStacks = ItemViewModifier.MODIFIER.invoker().get();
		list.addAll(modifierStacks);
		list.addAll(PolydexIntegration.ITEM_STACKS);

		List<ItemStack> removeItemStacks = ItemViewRemoveModifier.ITEM_STACK_REMOVER.invoker().get();
		removeItemStacks.addAll(PolydexIntegration.REMOVED_ITEM_STACKS);
		Iterator<ItemStack> itemStackIterator = removeItemStacks.iterator();
		while (itemStackIterator.hasNext()) {
			ItemStack next = itemStackIterator.next();
			for (ItemStack stack : list) {
				boolean b = ItemStack.isSameItemSameComponents(next, stack);
				if (b) itemStackIterator.remove();
			}
		}
		*///?}
	}

	public static boolean isPolymerServerItem(ItemStack stack) {
		//? fabric && <26.1 {
		/*return PolymerItemUtils.isPolymerServerItem(stack);
		*///?} else {
		return false;
		//?}
	}

	public static ItemStack getRealItemStack(ItemStack stack, RegistryAccess.Frozen registryManager) {
		//? fabric && <26.1 {
		/*return PolymerItemUtils.getRealItemStack(stack, registryManager);
		 *///?} else {
		return ItemStack.EMPTY;
		//?}
	}

	public static boolean isPolymerScreenOpen(LocalPlayer player) {
		//? if fabric && <26 {
		/*return (Minecraft.getInstance().screen instanceof RecipeViewScreen recipeViewScreen && recipeViewScreen.getMenu().getClientRecipeType() instanceof PolydexClientRecipeType) || (player.containerMenu instanceof FakeScreenHandler);
		*///?} else
		return false;
	}
}
