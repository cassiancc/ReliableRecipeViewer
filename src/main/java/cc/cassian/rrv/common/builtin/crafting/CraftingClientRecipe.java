package cc.cassian.rrv.common.builtin.crafting;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftingClientRecipe implements ReliableClientRecipe {

    private final Identifier id;
    private final int priority;
    private final HashMap<Integer, SlotContent> ingredients;
    private final SlotContent result;
    private final int width, height;
    private final boolean shapeless;
	private final int dependentIndex;

    /// Implement via the builder - [CraftingClientRecipe.Builder].
    private CraftingClientRecipe(Identifier id, int width, int height, HashMap<Integer, SlotContent> ingredients, SlotContent result, int dependentIndex, int priority, boolean shapeless) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.shapeless = shapeless;
        this.dependentIndex = dependentIndex;
        this.priority = priority;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return CraftingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        this.ingredients.forEach(slotFillContext::bindSlot);
		if (dependentIndex != -1)
        	slotFillContext.bindDependentSlot(9, ()->this.ingredients.get(dependentIndex).index(), this.result);
		else
			slotFillContext.bindSlot(9, this.result);
	}

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ReliableClientRecipe.super.renderRecipe(screen, recipePosition, guiGraphics, mouseX, mouseY, partialTicks);
        if (shapeless) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("crafting_shapeless"), 26, 14, 0, 0, 92, 0, 26, 14);
            if ((mouseX > 92 && mouseX < 122) && (mouseY>0 && mouseY < 14)) {
                guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), List.of(Component.translatable("view.rrv.type.crafting.shapeless")), mouseX+recipePosition.left(), mouseY+recipePosition.top());
            }
        }

    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.ingredients.values().stream().toList();
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(CraftingScreen.class, InventoryScreen.class);
    }

    @Override
    public boolean canTransferToScreen(AbstractContainerScreen<?> screen) {
        return screen instanceof CraftingScreen || this.width <= 2 && this.height <= 2;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap map, AbstractContainerScreen<?> screen) {

        if (!(screen instanceof InventoryScreen invScreen)) {
            map.linkSlots(0, 1);
            map.linkSlots(1, 2);
            map.linkSlots(2, 3);
            map.linkSlots(3, 4);
            map.linkSlots(4, 5);
            map.linkSlots(5, 6);
            map.linkSlots(6, 7);
            map.linkSlots(7, 8);
            map.linkSlots(8, 9);

        } else {
            //For smaller grid
            map.linkSlots(0, 1);
            map.linkSlots(1, 2);
            map.linkSlots(3, 3);
            map.linkSlots(4, 4);
        }


    }

    public static class Builder {
        private final Identifier id;
        private int width = 3;
        private int height = 3;
        private HashMap<Integer, SlotContent> ingredients = new HashMap<>();
        private boolean shapeless = false;
        private SlotContent result;
        private int dependentIndex = -1;
        private Integer priority = null;

        /// General constructor.
        public Builder(Identifier id) {
            this.id = id;
        }

        /// Constructor for shapeless recipes.
        public Builder(Identifier id, List<SlotContent> ingredients) {
            this(id);
            var size = ingredients.size();
            switch (size) {
                case 1 -> setSize(1, 1);
                case 2 -> setSize(2, 1);
                case 3 -> setSize(3,1);
                case 4 -> setSize(2,2);
                case 5, 6 -> setSize(3,2);
                default -> setSize(3, 3);
            }

            AtomicInteger i = new AtomicInteger();
            ingredients.forEach((ingredient) -> this.ingredients.put(i.getAndIncrement(), (ingredient)));
            this.shapeless = true;
        }

        /// Constructor for shapeless recipes.
        public Builder(Identifier id, Ingredient... ingredients) {
            this(id, Arrays.stream(ingredients).map(SlotContent::of).toList());
        }

        /// Constructor for shapeless recipes.
        public Builder(Identifier id, SlotContent... ingredients) {
            this(id, Arrays.stream(ingredients).toList());
        }

        /// Constructor for shaped recipes.
        public Builder(Identifier id, HashMap<Integer, SlotContent> ingredients) {
            this(id);
            this.ingredients = ingredients;
        }

        /// Set the size of the recipe grid. If this is a shapeless recipe, this is set automatically.
        public Builder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setResult(SlotContent result) {
            this.result = result;
            return this;
        }

        public Builder setResult(ItemStackTemplate result) {
            return setResult(SlotContent.of(result));
        }

        public Builder setResult(List<ItemStackTemplate> result) {
            return setResult(SlotContent.ofTemplates(result));
        }

        public Builder setDependentIndex(int dependentIndex) {
            this.dependentIndex = dependentIndex;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public CraftingClientRecipe build() {
            if (priority == null) {
                if (shapeless) setPriority(1);
                else setPriority(0);
            }
            return new CraftingClientRecipe(id, width, height, ingredients, result, dependentIndex, priority, shapeless);
        }
    }
}
