package cc.cassian.rrv.common.recipe.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class ViewContainer implements Container {

    private final NonNullList<ItemStack> items;
    private final int size;

    protected ViewContainer(int size){
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.takeItem(this.items, 0);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, 0);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.items.set(slot, itemStack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }


}
