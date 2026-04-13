package cc.cassian.rrv.common.builtin.crafting;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftingClientRecipe implements ReliableClientRecipe {

    private final Identifier id;
    private HashMap<Integer, SlotContent> ingredients = new HashMap<>();
    private final SlotContent result;
    private final int width, height;
    private final boolean shapeless;
	private int dependentIndex = -1;

    /**
     * Constructor for decorated pot recipes.
     */
    public CraftingClientRecipe(Identifier id, int width, int height, HashMap<Integer, SlotContent> ingredients, SlotContent result, int dependentIndex) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.shapeless = false;
        this.dependentIndex = dependentIndex;
    }

    /**
     * Constructor for shapeless recipes.
     */
    public CraftingClientRecipe(Identifier id, List<SlotContent> ingredients, SlotContent result) {
        this.id = id;
        this.shapeless = true;
        var size = ingredients.size();
        switch (size) {
            case 1 -> {
                this.width = 1;
                this.height = 1;
            }
            case 2 -> {
                this.width = 2;
                this.height = 1;
            }
            case 3 -> {
                this.width = 3;
                this.height = 1;
            }
            case 4 -> {
                this.width = 2;
                this.height = 2;
            }
            case 5, 6 -> {
                this.width = 3;
                this.height = 2;
            }
            default -> {
                this.width = 3;
                this.height = 3;
            }
        }


        AtomicInteger i = new AtomicInteger();
        ingredients.forEach((ingredient) -> {
            this.ingredients.put(i.getAndIncrement(), (ingredient));
        });

        this.result = result;
    }

    /**
     * Constructor for dye recipes.
     */
    public CraftingClientRecipe(Identifier id, Ingredient target, Ingredient dye, List<ItemStackTemplate> results, int dependentIndex) {
        this.id = id;
        this.width = 2;
        this.height = 1;
        this.shapeless = true;
        this.ingredients.put(0, SlotContent.of(target));
        this.ingredients.put(1, SlotContent.of(dye));
        this.dependentIndex = dependentIndex;

        this.result = SlotContent.ofTemplates(results);
    }

    /**
     * Constructor for shapeless transmutation recipes.
     */
    public CraftingClientRecipe(Identifier id, Ingredient input, Ingredient material, List<ItemStackTemplate> results) {
        this(id, input, material, results, -1);
    }

    /**
     * Constructor for shapeless recipes.
     */
    public CraftingClientRecipe(Identifier id, List<Ingredient> ingredients, ItemStackTemplate result) {
        this(id, ingredients.stream().map(SlotContent::of).toList(), SlotContent.of(result));
    }

    /**
     * Constructor for shaped recipes.
     */
    public CraftingClientRecipe(Identifier id, int width, int height, HashMap<Integer, SlotContent> ingredients, SlotContent result) {
        this(id, width, height, ingredients, result, -1);
    }

    @Override
    public ReliableClientRecipeType getViewType() {
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
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("rrv", "crafting_shapeless"), 26, 14, 0, 0, 92, 0, 26, 14);
            if ((mouseX > 92 && mouseX < 122) && (mouseY>0 && mouseY < 14)) {
                guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), List.of(Component.translatable("view.rrv.type.crafting.shapeless")), mouseX+recipePosition.left(), mouseY+recipePosition.top());
            }
        }

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
}
