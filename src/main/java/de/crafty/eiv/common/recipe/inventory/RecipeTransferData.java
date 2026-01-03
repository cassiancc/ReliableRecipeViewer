package de.crafty.eiv.common.recipe.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class RecipeTransferData {

    public static final RecipeTransferData EMPTY = null;

    private final HashMap<Integer, Boolean> slotResults;
    private final HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots;

    private boolean success = false;
    private RecipeTransferData stackedData;

    private RecipeTransferData(HashMap<Integer, Boolean> slotResults, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {
        this.slotResults = slotResults;
        this.usedPlayerSlots = usedPlayerSlots;

        this.stackedData = this;

        int successAmount = 0;
        for (int slotId : slotResults.keySet()) {
            if(slotResults.get(slotId))
                successAmount++;
        }

        if(successAmount == slotResults.size())
            this.success = true;
    }

    public void setStackedData(RecipeTransferData data) {
        this.stackedData = data;
    }

    public RecipeTransferData getStackedData(){
        return this.stackedData;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public HashMap<Integer, Boolean> getSlotResults() {
        return this.slotResults;
    }

    public HashMap<Integer, HashMap<Integer, ItemStack>> getUsedPlayerSlots() {
        return this.usedPlayerSlots;
    }

    public static class Builder {

        private final HashMap<Integer, Boolean> slotResults;
        private final HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots;

        protected Builder() {
            this.slotResults = new HashMap<>();
            this.usedPlayerSlots = new HashMap<>();
        }

        private Builder(HashMap<Integer, Boolean> slotResults, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {
            this.slotResults = slotResults;
            this.usedPlayerSlots = usedPlayerSlots;
        }

        protected void noticeSlot(int slotId) {
            this.slotResults.put(slotId, false);
        }


        protected void findContent(int recipeSlot, HashMap<Integer, ItemStack> playerSlots) {
            this.slotResults.put(recipeSlot, true);
            this.usedPlayerSlots.put(recipeSlot, playerSlots);
        }

        protected RecipeTransferData build() {
            return new RecipeTransferData(this.slotResults, this.usedPlayerSlots);
        }

        protected Builder duplicate(){
            return new Builder(this.slotResults, new HashMap<>());
        }
    }
}
