package cc.cassian.rrv.common.builtin.villager;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class VillagerClientRecipeType implements ReliableClientRecipeType {

    public static final VillagerClientRecipeType INSTANCE = new VillagerClientRecipeType();

    private static final ReferenceCondition REFERENCE_CONDITION = (stack, viewRecipe) -> {
        if(!(stack.getItem() instanceof BlockItem blockItem))
            return true;

        Optional<Holder<PoiType>> optional = PoiTypes.forState(blockItem.getBlock().defaultBlockState());
        if(optional.isEmpty())
            return true;

        Holder<PoiType> holder = optional.get();
        if(!(viewRecipe instanceof VillagerClientRecipe villagerViewRecipe) || Minecraft.getInstance().level == null)
            return true;

        VillagerProfession profession = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.VILLAGER_PROFESSION).getValue(villagerViewRecipe.villagerOffer.profession());

        if(profession == null)
            return true;

        return profession.heldJobSite().test(holder);
    };
    public static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/villager.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.trading");
    }

    @Override
    public int getDisplayWidth() {
        return 146;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public Identifier getGuiTexture() {
        return BACKGROUND;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Currency
        slotDefinition.addItemSlot(0, 38, 26);
        slotDefinition.addItemSlot(1, 64, 26);

        //offer
        slotDefinition.addItemSlot(2, 122, 27);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("villager_trading");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.EMERALD);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.VILLAGER_SPAWN_EGG),
                new ItemStack(Items.COMPOSTER),
                new ItemStack(Items.FLETCHING_TABLE),
                new ItemStack(Items.LECTERN),
                new ItemStack(Items.BARREL),
                new ItemStack(Items.SMOKER),
                new ItemStack(Items.BREWING_STAND),
                new ItemStack(Items.CAULDRON),
                new ItemStack(Items.SMITHING_TABLE),
                new ItemStack(Items.CARTOGRAPHY_TABLE),
                new ItemStack(Items.STONECUTTER),
                new ItemStack(Items.BLAST_FURNACE),
                new ItemStack(Items.LOOM),
                new ItemStack(Items.GRINDSTONE)
        );
    }

    @Override
    public ReferenceCondition getCraftReferenceCondition() {
        return REFERENCE_CONDITION;
    }

    public ButtonData placeRecipeShareButton(RecipeViewMenu.DisplayInfo info) {
        return new ButtonData(info.guiRight() - 16, info.guiTop() + 5, true);
    }
}
