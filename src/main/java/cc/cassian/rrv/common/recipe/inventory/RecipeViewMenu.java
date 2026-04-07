package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.recipe.PolydexClientRecipe;
import cc.cassian.rrv.common.integration.polymer.recipe.PolydexClientRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class RecipeViewMenu extends AbstractContainerMenu {

    //For screen and space calculation
    protected static final int MAX_POSSIBLE_HEIGHT = 214;
    protected static final int BUFFER_ZONE = 16;
    protected static final int TOP_SPACE = 24;
    protected static final int BOTTOM_SPACE = 24;

    private final Player player;
    private ViewContainer viewContainer;

    private List<? extends ReliableClientRecipe> recipes;
    private ReliableClientRecipeType clientRecipeType;

    private int maxPossiblePerPage;
    private int maxPageIndex;
    private int currentPage;

    private final List<ReliableClientRecipe> currentDisplay;

    private final LinkedHashMap<ReliableClientRecipeType, List<ReliableClientRecipe>> sortedByType;
    private final List<ReliableClientRecipeType> viewTypeOrder;
    private int currentTypeIndex;

    private int menuWidth, menuHeight;
    private final ItemStack origin;
    private final ActionType originType;
    private final HashMap<Integer, AdditionalStackModifier> additionalStackModifiers;
    private final HashMap<Integer, OptionalSlotRenderer> optionalSlotRenderers;

    private RecipeViewScreen viewScreen;
    private final Screen parentScreen;
    private final ArrayList<RecipeViewScreen> viewHistory;

    private int currentCraftReference;
    private final List<RecipeTransferData> transferData;

    public RecipeViewMenu(Screen parentScreen, int containerId, Inventory inventory, List<? extends ReliableClientRecipe> recipes, ItemStack origin, ActionType originType, ArrayList<RecipeViewScreen> viewHistory) {
        this(parentScreen,containerId, inventory,recipes, origin, originType, viewHistory, ReliableClientRecipeType.NONE);
    }

    public RecipeViewMenu(Screen parentScreen, int containerId, Inventory inventory, List<? extends ReliableClientRecipe> recipes, ItemStack origin, ActionType originType, ArrayList<RecipeViewScreen> viewHistory, ReliableClientRecipeType clientRecipeType) {
        super(ReliableRecipeViewer.RECIPE_VIEW_MENU, containerId);

        this.viewHistory = viewHistory;

        this.parentScreen = parentScreen;
        this.transferData = new ArrayList<>();

        this.currentCraftReference = 0;

        this.origin = origin;
        this.originType = originType;
        this.additionalStackModifiers = new HashMap<>();
        this.optionalSlotRenderers = new HashMap<>();

        this.sortedByType = new LinkedHashMap<>();
        HashMap<ReliableClientRecipeType, HashMap<Integer, List<ReliableClientRecipe>>> prioOrder = new HashMap<>();

        recipes.forEach(recipe -> {
            List<ReliableClientRecipe> list = prioOrder.getOrDefault(recipe.getViewType(), new HashMap<>()).getOrDefault(recipe.getPriority(), new ArrayList<>());
            list.add(recipe);
            HashMap<Integer, List<ReliableClientRecipe>> map = prioOrder.getOrDefault(recipe.getViewType(), new HashMap<>());
            map.put(recipe.getPriority(), list);
            prioOrder.put(recipe.getViewType(), map);
        });

        prioOrder.forEach((viewType, map) -> {
            List<ReliableClientRecipe> list = new ArrayList<>();
            map.values().forEach(list::addAll);
            this.sortedByType.put(viewType, list);
        });

        //Sorting recipe types
        this.viewTypeOrder = new ArrayList<>();
        List<ReliableClientRecipeType> unsortedTypes = this.sortedByType.keySet().stream().filter(Configs.CATEGORIES::enabled).toList();
        HashMap<Identifier, ReliableClientRecipeType> byId = new HashMap<>();
        unsortedTypes.forEach(recipeType -> {
            byId.put(recipeType.getId(), recipeType);
        });

        List<Identifier> ids = new ArrayList<>(byId.keySet());
        ids.sort(Configs.CATEGORIES::compareTo);

        ids.forEach(id -> {
            this.viewTypeOrder.add(byId.get(id));
        });

        // manually placed a clicked recipe type first
        if (!clientRecipeType.equals(ReliableClientRecipeType.NONE)) {
            this.viewTypeOrder.remove(clientRecipeType);
            this.viewTypeOrder.addFirst(clientRecipeType);
        }

        this.currentTypeIndex = 0;

        this.currentPage = 0;
        this.currentDisplay = new ArrayList<>();

        if (ModCompat.POLYDEX) {
            this.sortedByType.put(PolydexClientRecipeType.INSTANCE, List.of(new PolydexClientRecipe(originType, origin)));
            this.viewTypeOrder.add(PolydexClientRecipeType.INSTANCE);
        }

        if (recipes.isEmpty())
            ReliableRecipeViewer.LOGGER.error("Attempting to open Menu with 0 recipes");

        player = inventory.player;
        this.updateByViewType();

        if (!this.sortedByType.isEmpty())
            return;

        this.viewContainer = new ViewContainer(0);
        this.clientRecipeType = ReliableClientRecipeType.NONE;

    }

    public RecipeViewMenu(int containerId, Inventory inventory) {
        this(null, containerId, inventory, ReliableClientRecipe.PLACEHOLDER, ItemStack.EMPTY, ActionType.ANY, new ArrayList<>());
    }


    public int getCurrentCraftReference() {
        return this.currentCraftReference;
    }

    public Screen getParentScreen() {
        return this.parentScreen;
    }

    public ArrayList<RecipeViewScreen> getViewHistory() {
        return this.viewHistory;
    }

    public boolean goBack() {
        if (this.viewScreen != null && this.viewHistory.indexOf(this.viewScreen) > 0) {
            RRVClientUtil.setScreen(this.viewHistory.get(this.viewHistory.indexOf(this.viewScreen) - 1));
            return true;
        }

        return false;
    }

    public boolean goForward() {
        if (this.viewScreen != null && this.viewHistory.size() - 1 > this.viewHistory.indexOf(this.viewScreen)) {
            RRVClientUtil.setScreen(this.viewHistory.get(this.viewHistory.indexOf(this.viewScreen) + 1));
            return true;
        }

        return false;
    }

    public void setViewScreen(RecipeViewScreen viewScreen) {
        this.viewScreen = viewScreen;
        this.viewHistory.add(viewScreen);
    }

    public ItemStack getOrigin() {
        return this.origin;
    }

    public AdditionalStackModifier getAdditionalStackModifier(int slot) {
        return this.additionalStackModifiers.getOrDefault(slot, AdditionalStackModifier.NONE);
    }

    public boolean isOptionalSlot(int slot) {
        return this.optionalSlotRenderers.containsKey(slot);
    }

    public OptionalSlotRenderer getOptionalSlotRenderer(int slot) {
        return this.optionalSlotRenderers.getOrDefault(slot, OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.viewContainer.stillValid(player);
    }


    public int getMaxPossiblePerPage() {
        return this.maxPossiblePerPage;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getMaxPageIndex() {
        return this.maxPageIndex;
    }

    public void nextReference() {
        this.currentCraftReference = Math.min(this.currentCraftReference + 1, this.clientRecipeType.getCraftReferences().size() - this.getDisplayableCraftReferences());
        this.updateReferences();
    }

    public void prevReference() {
        this.currentCraftReference = Math.max(this.currentCraftReference - 1, 0);
        this.updateReferences();
    }

    public void nextPage() {
        this.currentPage = Math.min(this.currentPage + 1, this.maxPageIndex);
        this.updateByPage();
    }

    public void prevPage() {
        this.currentPage = Math.max(this.currentPage - 1, 0);
        this.updateByPage();
    }

    public void nextRecipe() {
        int prevPage = this.currentPage;
        this.currentPage = Math.min(this.currentPage + 1, this.maxPageIndex);

        if (prevPage != this.currentPage)
            this.updateByPage();

    }

    public void setClientRecipeType(int typeId) {
        int prevIndex = this.currentTypeIndex;
        this.currentTypeIndex = typeId;

        if (prevIndex != this.currentTypeIndex)
            this.updateByViewType();
    }

    public boolean hasNextRecipe() {
        return this.currentPage < this.maxPageIndex;
    }

    public boolean hasPrevRecipe() {
        return this.currentPage > 0;
    }

    public List<ReliableClientRecipeType> getViewTypeOrder() {
        return this.viewTypeOrder;
    }

    public int getCurrentTypeIndex() {
        return this.currentTypeIndex;
    }

    protected List<ReliableClientRecipe> getCurrentDisplay() {
        return this.currentDisplay;
    }

    private List<ReliableClientRecipe> getRecipeDisplay() {
        List<ReliableClientRecipe> recipesOnPage = new ArrayList<>();
        for (int i = this.currentPage * this.maxPossiblePerPage; i < Math.min(this.getRecipes().size(), (this.currentPage + 1) * this.maxPossiblePerPage); i++) {
            recipesOnPage.add(this.getRecipes().get(i));
        }

        return recipesOnPage;
    }

    protected void updateByPage() {
        this.additionalStackModifiers.clear();
        this.optionalSlotRenderers.clear();

        this.slots.clear();

        this.currentDisplay.forEach(ReliableClientRecipe::fadeRecipe);
        this.currentDisplay.clear();

        this.currentDisplay.addAll(this.getRecipeDisplay());
        this.currentDisplay.forEach(ReliableClientRecipe::initRecipe);

        this.currentCraftReference = 0;

        for (int i = 0; i < this.currentDisplay.size(); i++) {

            ReliableClientRecipe recipe = this.currentDisplay.get(i);
            recipe.getIngredients().forEach(slotContent -> slotContent.bindOrigin(this.origin, this.originType));
            recipe.getResults().forEach(slotContent -> slotContent.bindOrigin(this.origin, this.originType));

            recipe.getIngredients().forEach(slotContent -> slotContent.setType(ActionType.INPUT));
            recipe.getResults().forEach(slotContent -> slotContent.setType(ActionType.RESULT));

            SlotDefinition slotDefinition = new SlotDefinition();
            this.clientRecipeType.placeSlots(slotDefinition);
            for (Slot slot : slotDefinition.getItemSlots()) {
                int id = slot.getContainerSlot() + (i * this.getClientRecipeType().getSlotCount());

                this.addSlot(new Slot(slot.container, id, slot.x + this.guiOffsetLeft(), slot.y + this.guiOffsetTop(i)));
            }

            SlotFillContext slotFillContext = new SlotFillContext();
            recipe.bindSlots(slotFillContext);

            for (int j = 0; j < this.getClientRecipeType().getSlotCount(); j++) {
                int slotId = j + (i * this.getClientRecipeType().getSlotCount());
                this.viewContainer.setItem(slotId, slotFillContext.contentBySlot(j).getByIndex(slotFillContext.contentBySlot(j).index()));

                if (slotFillContext.getAdditionalTooltips().containsKey(j))
                    this.additionalStackModifiers.put(slotId, slotFillContext.getAdditionalTooltips().get(j));

                if (slotFillContext.getOptionalSlotRenderers().containsKey(j))
                    this.optionalSlotRenderers.put(slotId, slotFillContext.getOptionalSlotRenderers().get(j));

            }


        }

        this.transferData.clear();
        for (int i = 0; i < this.getCurrentDisplay().size(); i++) {
            this.transferData.add(this.checkMatchingContent(i));
        }

        if (this.viewScreen != null)
            this.viewScreen.checkGui();

        this.updateDependencies();


        for (int i = 0; i < this.getDisplayableCraftReferences(); i++) {
            this.addSlot(new Slot(this.viewContainer, this.clientRecipeType.getSlotCount() * this.getCurrentDisplay().size() + i, -25 + 4, 4 + 4 + i * 24 + i));
        }

        this.updateReferences();
    }


    public int getDisplayableCraftReferences() {
        List<ItemStack> craftReferences = this.getClientRecipeType().getCraftReferences();
        return Math.min(craftReferences.size(), (this.getHeight() - 4) / 25);
    }

    private void updateReferences() {
        List<ItemStack> craftReferences = this.clientRecipeType.getCraftReferences();
        for (int i = this.currentCraftReference; i < Math.min(this.clientRecipeType.getCraftReferences().size(), this.currentCraftReference + this.getDisplayableCraftReferences()); i++) {
            this.getSlot(this.clientRecipeType.getSlotCount() * this.getCurrentDisplay().size() + (i - this.currentCraftReference)).set(craftReferences.get(i));
        }
    }

    public List<RecipeTransferData> getTransferData() {
        return this.transferData;
    }

    private RecipeTransferData checkMatchingContent(int displayId) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null)
            return RecipeTransferData.EMPTY;

        ReliableClientRecipe currentLooking = this.getCurrentDisplay().get(displayId);


        RecipeViewMenu.SlotFillContext context = new RecipeViewMenu.SlotFillContext();
        currentLooking.bindSlots(context);

        LocalPlayer player = minecraft.player;

        NonNullList<ItemStack> stackSupply = NonNullList.withSize(player.getInventory().getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < stackSupply.size(); slot++) {
            stackSupply.set(slot, player.getInventory().getItem(slot).copy());
        }


        RecipeTransferData.Builder dataBuilder = new RecipeTransferData.Builder();
        context.getContents().keySet().stream().filter(slot -> context.getContents().get(slot).getType() != ActionType.RESULT).forEach(dataBuilder::noticeSlot);

        HashMap<Integer, List<ItemStack>> validAndAvailableContent = new HashMap<>();

        //Filter relevant items to check and exclude air + results
        for (int slot : context.getContents().keySet()) {

            SlotContent content = context.getContents().get(slot);

            if (content.getType() == ActionType.RESULT)
                continue;

            if (content.isEmpty()) {
                dataBuilder.findContent(slot, new HashMap<>());
                continue;
            }

            List<ItemStack> availableItems = new ArrayList<>();
            content.getValidContents().forEach(stack -> {
                StackValidator stackValidator = context.getStackValidators().getOrDefault(slot, null);

                if (stackSupply.stream().anyMatch(stack1 -> stack1.is(stack.getItem())) && (stackValidator == null || stackValidator.validate(stack)))
                    availableItems.add(stack);
            });

            validAndAvailableContent.put(slot, availableItems);
        }

        List<Integer> slots = new ArrayList<>();
        context.getContents().forEach((slot, slotContent) -> {
            if (slotContent.getType() != ActionType.RESULT)
                slots.add(slot);
        });


        HashMap<Integer, HashMap<Integer, ItemStack>> bestMatch = new HashMap<>();
        this.check(slots, 0, validAndAvailableContent, new HashMap<>(), bestMatch, stackSupply);
        bestMatch.forEach(dataBuilder::findContent);

        RecipeTransferData transferData = dataBuilder.build();

        if (transferData.isSuccess() && !transferData.getUsedPlayerSlots().isEmpty()) {

            HashMap<Integer, ItemStack> requiredStacks = new HashMap<>();
            for (int recipeSlot : transferData.getUsedPlayerSlots().keySet()) {

                HashMap<Integer, ItemStack> usedSlots = transferData.getUsedPlayerSlots().get(recipeSlot);

                ItemStack required = usedSlots.values().stream().findFirst().orElseGet(() -> ItemStack.EMPTY).copy();

                int amount = 0;
                for (ItemStack stack : usedSlots.values()) {
                    amount += stack.getCount();
                }

                if (!required.isEmpty())
                    requiredStacks.put(recipeSlot, required.copyWithCount(amount));
            }

            boolean checking = true;
            HashMap<Integer, HashMap<Integer, ItemStack>> stackable = new HashMap<>();

            int runs = 0;

            while (checking) {

                for (int recipeSlot : requiredStacks.keySet()) {

                    if ((runs + 1) * requiredStacks.get(recipeSlot).getCount() > requiredStacks.get(recipeSlot).getMaxStackSize() - requiredStacks.get(recipeSlot).getCount()) {
                        checking = false;
                        break;
                    }
                }

                if (!checking)
                    break;

                HashMap<Integer, HashMap<Integer, ItemStack>> currentStackable = new HashMap<>();

                for (int recipeSlot : requiredStacks.keySet()) {

                    ItemStack requiredStack = requiredStacks.get(recipeSlot);

                    HashMap<Integer, ItemStack> found = this.invCheckAndFind(stackSupply, requiredStack, true);
                    if (found.isEmpty()) {
                        checking = false;
                        break;
                    }

                    currentStackable.put(recipeSlot, found);
                }

                if (checking) {
                    //Add to complete map
                    currentStackable.forEach((recipeSlot, usedPlayerSlots) -> {

                        HashMap<Integer, ItemStack> presentStackable = stackable.getOrDefault(recipeSlot, new HashMap<>());

                        usedPlayerSlots.forEach((playerSlot, stack) -> {
                            if (presentStackable.containsKey(playerSlot))
                                presentStackable.put(playerSlot, stack.copyWithCount(presentStackable.get(playerSlot).getCount() + stack.getCount()));
                            else
                                presentStackable.put(playerSlot, stack);
                        });

                        stackable.put(recipeSlot, presentStackable);
                    });

                    runs++;
                }
            }


            HashMap<Integer, HashMap<Integer, ItemStack>> stackedMatch = new HashMap<>();
            bestMatch.forEach((recipeSlot, usedPlayerSlots) -> {

                HashMap<Integer, ItemStack> playerSlots = new HashMap<>();

                usedPlayerSlots.forEach((playerSlot, stack) -> {
                    playerSlots.put(playerSlot, stack.copy());
                });
                stackedMatch.put(recipeSlot, playerSlots);

            });

            stackable.forEach((recipeSlot, usedPlayerSlots) -> {

                usedPlayerSlots.forEach((playerSlot, stack) -> {

                    if (stackedMatch.get(recipeSlot).containsKey(playerSlot))
                        stackedMatch.get(recipeSlot).put(playerSlot, stack.copyWithCount(stackedMatch.get(recipeSlot).get(playerSlot).getCount() + stack.getCount()));
                    else
                        stackedMatch.get(recipeSlot).put(playerSlot, stack);

                });

            });

            RecipeTransferData.Builder stackedBuilder = dataBuilder.duplicate();

            stackedMatch.forEach(stackedBuilder::findContent);
            transferData.setStackedData(stackedBuilder.build());
        }

        return transferData;
    }


    private void check(List<Integer> slots, int currentSlotIndex, HashMap<Integer, List<ItemStack>> validAndAvailableContent, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots, HashMap<Integer, HashMap<Integer, ItemStack>> bestMatch, NonNullList<ItemStack> stackSupply) {

        if (currentSlotIndex >= slots.size() || bestMatch.size() == slots.size())
            return;

        List<ItemStack> validStacks = validAndAvailableContent.getOrDefault(slots.get(currentSlotIndex), new ArrayList<>());

        for (ItemStack requiredStack : validStacks) {

            HashMap<Integer, ItemStack> found = this.invCheckAndFind(stackSupply, requiredStack, false);

            if (!found.isEmpty()) {
                usedPlayerSlots.put(slots.get(currentSlotIndex), found);
                if (usedPlayerSlots.size() > bestMatch.size())
                    bestMatch.putAll(usedPlayerSlots);

                this.check(slots, currentSlotIndex + 1, validAndAvailableContent, usedPlayerSlots, bestMatch, stackSupply);
            }
        }
        this.check(slots, currentSlotIndex + 1, validAndAvailableContent, usedPlayerSlots, bestMatch, stackSupply);

    }

    private HashMap<Integer, ItemStack> invCheckAndFind(NonNullList<ItemStack> stackSupply, ItemStack requiredStack, boolean checkComponents) {

        HashMap<Integer, ItemStack> usedPlayerSlots = new HashMap<>();
        int requiredAmount = requiredStack.getCount();

        ItemStack firstFound = ItemStack.EMPTY;

        for (int playerSlot = 0; playerSlot < stackSupply.size(); playerSlot++) {

            if (requiredAmount <= 0)
                break;

            ItemStack playerStack = stackSupply.get(playerSlot);
            ItemStack foundStack = playerStack.copy();

            if (!(checkComponents ? ItemStack.isSameItemSameComponents(playerStack, requiredStack) : ItemStack.isSameItem(playerStack, requiredStack)))
                continue;

            if (firstFound.isEmpty())
                firstFound = foundStack.copy();

            if (!ItemStack.isSameItemSameComponents(foundStack, firstFound))
                continue;

            int prevReq = requiredAmount;
            requiredAmount -= Math.min(requiredAmount, playerStack.getCount());

            playerStack.setCount(playerStack.getCount() - (prevReq - requiredAmount));
            if (playerStack.getCount() <= 0)
                stackSupply.set(playerSlot, ItemStack.EMPTY);

            foundStack.setCount(prevReq - requiredAmount);

            OptionalInt menuSlotId = player.containerMenu.findSlot(player.getInventory(), playerSlot);

            if (menuSlotId.isPresent())
                usedPlayerSlots.put(menuSlotId.getAsInt(), foundStack);
            else {
                //Just for safety, we do not want to lose any player items
                this.returnToCache(usedPlayerSlots, stackSupply);
                return new HashMap<>();
            }

        }

        if (requiredAmount == 0)
            return usedPlayerSlots;
        else {
            this.returnToCache(usedPlayerSlots, stackSupply);

            return new HashMap<>();
        }
    }

    private void returnToCache(HashMap<Integer, ItemStack> usedPlayerSlots, NonNullList<ItemStack> stackSupply) {
        usedPlayerSlots.forEach((playerSlot, stack) -> {

            if (stackSupply.get(playerSlot).isEmpty())
                stackSupply.set(playerSlot, stack);
            else
                stackSupply.get(playerSlot).setCount(stackSupply.get(playerSlot).getCount() + stack.getCount());

        });
    }

    private void resetContentPointers() {
        this.recipes.forEach(iRrvRecipe -> {
            iRrvRecipe.getIngredients().forEach(SlotContent::resetPointer);
            iRrvRecipe.getResults().forEach(SlotContent::resetPointer);
        });
    }

    protected void updateByViewType() {

        this.currentPage = 0;
        this.recipes = this.sortedByType.getOrDefault(this.viewTypeOrder.get(this.currentTypeIndex), new ArrayList<>());
        this.resetContentPointers();

        Optional<? extends ReliableClientRecipe> optional = recipes.stream().findFirst();

        if (optional.isPresent()) {
            this.clientRecipeType = optional.get().getViewType();
            this.maxPossiblePerPage = this.calculateRecipesPerPage();

            if (maxPossiblePerPage != 0) {
                int i = this.getRecipes().size() / this.maxPossiblePerPage;
                if (this.getRecipes().size() % this.maxPossiblePerPage != 0)
                    i++;

                this.maxPageIndex = i - 1;
            }


            this.setMenuSizes();
            this.viewContainer = new ViewContainer(this.clientRecipeType.getSlotCount() * this.maxPossiblePerPage + this.getDisplayableCraftReferences());

            this.updateByPage();

        }
    }

    private void setMenuSizes() {
        this.menuHeight = TOP_SPACE + this.getRecipeDisplay().size() * this.getClientRecipeType().getDisplayHeight() + (this.getRecipeDisplay().size() * RecipeViewMenu.BUFFER_ZONE) + (BOTTOM_SPACE - BUFFER_ZONE);

        this.menuWidth = 176;
    }

    public int getHeight() {
        return this.menuHeight;
    }

    public int getWidth() {
        return this.menuWidth;
    }

    /**
	 * Returns how far the viewtype-specific texture is away from the border
	 */
    protected int guiOffsetLeft() {
        return (this.menuWidth - this.getClientRecipeType().getDisplayWidth()) / 2;
    }

    protected int guiOffsetTop(int displayIndex) {
        return TOP_SPACE + (displayIndex * (this.getClientRecipeType().getDisplayHeight() + BUFFER_ZONE));
    }

    protected void tickContents() {

        for (int i = 0; i < this.currentDisplay.size(); i++) {
            ReliableClientRecipe recipe = this.currentDisplay.get(i);

            SlotFillContext slotFillContext = new SlotFillContext();
            recipe.bindSlots(slotFillContext);

            for (int j = 0; j < this.getClientRecipeType().getSlotCount(); j++) {

                //Exclude DependencySlots
                if (!slotFillContext.contentDependencies.containsKey(j))
                    this.viewContainer.setItem(j + (i * this.getClientRecipeType().getSlotCount()), slotFillContext.contentBySlot(j).next());
            }

        }

        this.updateDependencies();
    }

    protected void updateDependencies() {
        for (int i = 0; i < this.currentDisplay.size(); i++) {
            ReliableClientRecipe recipe = this.currentDisplay.get(i);

            SlotFillContext slotFillContext = new SlotFillContext();
            recipe.bindSlots(slotFillContext);

            for (int j = 0; j < this.getClientRecipeType().getSlotCount(); j++) {

                if (slotFillContext.contentDependencies.containsKey(j))
                    this.viewContainer.setItem(j + (i * this.getClientRecipeType().getSlotCount()), slotFillContext.contentBySlot(j).getByIndex(Math.min(slotFillContext.contentDependencies.get(j).get(), slotFillContext.contentBySlot(j).size() - 1)));
            }
        }
    }

    public List<? extends ReliableClientRecipe> getRecipes() {
        return this.recipes;
    }

    public ReliableClientRecipeType getClientRecipeType() {
        return this.clientRecipeType;
    }

    public ViewContainer getViewContainer() {
        return this.viewContainer;
    }


    private int calculateRecipesPerPage() {
        if (this.getRecipes().isEmpty())
            return 0;

        int recipeHeight = this.getClientRecipeType().getDisplayHeight();

        int technicallyFitting = Math.min(this.getRecipes().size(), MAX_POSSIBLE_HEIGHT / recipeHeight);
        int imageheightRequired = (technicallyFitting * recipeHeight) + (technicallyFitting * BUFFER_ZONE + TOP_SPACE + BOTTOM_SPACE);

        while (imageheightRequired > MAX_POSSIBLE_HEIGHT) {
            technicallyFitting -= 1;

            imageheightRequired = (technicallyFitting * recipeHeight) + (technicallyFitting * BUFFER_ZONE + TOP_SPACE + BOTTOM_SPACE);
        }

        return technicallyFitting;
    }

    public void updateTransferCache() {
        this.transferData.clear();
        for (int i = 0; i < this.getCurrentDisplay().size(); i++) {
            this.transferData.add(this.checkMatchingContent(i));
        }

        this.viewScreen.checkGui();
    }


    public class SlotDefinition {

        private final HashMap<Integer, Slot> itemSlots;

        private SlotDefinition() {
            this.itemSlots = new HashMap<>();
        }

        public void addItemSlot(int slotId, int x, int y) {
            this.itemSlots.put(slotId, new Slot(RecipeViewMenu.this.viewContainer, slotId, x, y));
        }

        private List<Slot> getItemSlots() {
            return this.itemSlots.values().stream().toList();
        }


    }

    public static class SlotFillContext {

        private final HashMap<Integer, SlotContent> contents;
        private final HashMap<Integer, Supplier<Integer>> contentDependencies;
        private final HashMap<Integer, AdditionalStackModifier> additionalTooltips;
        private final HashMap<Integer, StackValidator> stackValidators;

        private final HashMap<Integer, OptionalSlotRenderer> optionalSlotRenderers;

        protected SlotFillContext() {
            this.contents = new HashMap<>();
            this.contentDependencies = new HashMap<>();

            this.additionalTooltips = new HashMap<>();
            this.stackValidators = new HashMap<>();

            this.optionalSlotRenderers = new HashMap<>();
        }

        public void bindSlot(int slotId, SlotContent slotContent) {
            this.contents.put(slotId, slotContent);
        }

        /**
		 * Typo'd, use {@link #bindDependentSlot(int, Supplier, SlotContent)}
		 */
        @Deprecated(since = "6.4.0", forRemoval = true)
        public void bindDepedantSlot(int slotId, Supplier<Integer> dependentIndex, SlotContent slotContent) {
            bindDependentSlot(slotId, dependentIndex, slotContent);
        }

        public void bindDependentSlot(int slotId, Supplier<Integer> dependentIndex, SlotContent slotContent) {
            this.contents.put(slotId, slotContent);
            this.contentDependencies.put(slotId, dependentIndex);
        }

        /**
         * Optional slots are only rendered if they contain an ItemStack.
         *
         * @param slotId               The slot id
         * @param slotContent          The slot content
         * @param optionalSlotRenderer The renderer used for this slot
         */
        public void bindOptionalSlot(int slotId, SlotContent slotContent, OptionalSlotRenderer optionalSlotRenderer) {
            this.contents.put(slotId, slotContent);
            this.optionalSlotRenderers.put(slotId, optionalSlotRenderer);
        }

        public HashMap<Integer, Supplier<Integer>> contentDependencies() {
            return this.contentDependencies;
        }

        public void addAdditionalStackModifier(int slotId, AdditionalStackModifier tooltipProvider) {
            this.additionalTooltips.put(slotId, tooltipProvider);
        }

        public void addStackValidator(int slotId, StackValidator stackValidator) {
            this.stackValidators.put(slotId, stackValidator);
        }

        protected HashMap<Integer, SlotContent> getContents() {
            return this.contents;
        }

        protected HashMap<Integer, OptionalSlotRenderer> getOptionalSlotRenderers() {
            return this.optionalSlotRenderers;
        }

        protected SlotContent contentBySlot(int slotId) {
            return this.contents.getOrDefault(slotId, SlotContent.of(List.of()));
        }

        private HashMap<Integer, AdditionalStackModifier> getAdditionalTooltips() {
            return this.additionalTooltips;
        }


        private HashMap<Integer, StackValidator> getStackValidators() {
            return this.stackValidators;
        }
    }


    /**
     * Used to render an optional slot
     */
    public interface OptionalSlotRenderer {

        RecipeViewMenu.OptionalSlotRenderer DEFAULT = (guiGraphics, mouseX, mouseY, partialTicks) -> {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.DEFAULT_SLOT_TEXTURE, 0, 0, 0, 0, 18, 18, 18, 18);
        };

        /**
         * Render method for rendering an optional slot
         *
         * @param guiGraphics  {@link GuiGraphicsExtractor} used for rendering
         * @param mouseX       current mouse position on x-direction (relative to the client recipe)
         * @param mouseY       current mouse position on y-direction (relative to the client recipe)
         * @param partialTicks partialTicks
         */
        void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks);

    }

    public interface AdditionalStackModifier {

        AdditionalStackModifier NONE = (stack, tooltip) -> {
        };

        void addTooltip(ItemStack stack, List<Component> tooltip);

    }

    public interface StackValidator {

        boolean validate(ItemStack stack);

    }
}
