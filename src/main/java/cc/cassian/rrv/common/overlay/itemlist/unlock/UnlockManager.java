package cc.cassian.rrv.common.overlay.itemlist.unlock;

import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class UnlockManager {

	public static final UnlockManager INSTANCE = new UnlockManager();

	protected List<ItemStackTemplate> availableItems = new ArrayList<>();

	public void unlockItem(ItemStack stack) {
		if (stack.isEmpty()) return;
		ItemStackTemplate template = createStack(stack);
		if (!this.availableItems.contains(template)) {
			this.availableItems().add(template);
			if (SidePanelOverlay.showUnlocks())
				updateIndex();
		}
	}

	public void unlockItems(Collection<ItemStack> nonEquipmentItems) {
		nonEquipmentItems.forEach(this::unlockItem);
	}

	private static ItemStackTemplate createStack(ItemStack stack) {
		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compoundTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			compoundTag.remove("rrv_result");
			if (compoundTag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
			else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
		}
		stack.setCount(1);
		return ItemStackTemplate.fromNonEmptyStack(stack);
	}

	private static void updateIndex() {
		Minecraft.getInstance().execute(() -> SidePanelOverlay.INSTANCE.updateSidePanelIndex(SidePanelOverlay.Reason.UNLOCK));
	}

	public List<ItemStackTemplate> availableItems() {
		return availableItems;
	}

	public List<ItemStack> displayItems() {
		return availableItems().stream().map(ItemStackTemplate::create).sorted(Comparator.comparing((ItemStack t) -> t.getItemName().getString())).toList();
	}

	public void removeItem(ItemStack stack) {
		removeItem(createStack(stack));
	}

	public void removeItem(ItemStackTemplate stack) {
		if (this.availableItems().contains(stack)) {
			this.availableItems().remove(stack);
			if (SidePanelOverlay.showUnlocks())
				updateIndex();
		}
	}
}
