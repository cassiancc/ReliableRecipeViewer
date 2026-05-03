package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.WorkstationDisplay;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;


import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecipeViewScreen extends AbstractContainerScreen<RecipeViewMenu> {

    private static final Identifier VIEW_LOCATION = Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/recipe_view.png");
    private static final Identifier UNSELECTED_TOP_TABS = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2");

    private static final Identifier SELECTED_TOP_TABS = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2");

    //Timestamp when opening the view
    private final long timestamp;

    private Button prevRecipe, nextRecipe;
    private Component guiTitle, page;

    private final List<AnimationTicker> animationTickers;
    private final HashMap<Identifier, Integer> animationTickCache;

    public final List<Button> transferButtons;

    //View Type
    private final List<RecipeTypeButton> recipeTypeButtons;
    private int viewTypePage;
    private Button prevTypePage, nextTypePage;
    private final ArrayList<Renderable> widgets = new ArrayList<>();
    private ItemSlot workstationSlot;

    public RecipeViewScreen(RecipeViewMenu recipeViewMenu, Inventory inventory, Component component) {
        super(recipeViewMenu, inventory, component);

        this.transferButtons = new ArrayList<>();
        this.recipeTypeButtons = new ArrayList<>();
        this.viewTypePage = 0;

        this.animationTickers = new ArrayList<>();
        this.animationTickCache = new HashMap<>();

        this.imageHeight = this.getMenu().getHeight();
        this.imageWidth = this.getMenu().getWidth();

        this.guiTitle = component;
        this.page = this.createPageComponent();


        this.timestamp = inventory.player.level().getGameTime();
        recipeViewMenu.setViewScreen(this);
    }


    private Component createPageComponent() {
        return Component.literal((this.getMenu().getCurrentPage() + 1) + "/" + (this.getMenu().getMaxPageIndex() + 1));
    }


    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent mouseButtonEvent) {

        if (ReliableRecipeViewerClient.GO_BACK_RECIPE.matchesMouse(mouseButtonEvent) && this.getMenu().goBack())
            return true;
        if (ReliableRecipeViewerClient.GO_FORWARD_RECIPE.matchesMouse(mouseButtonEvent) && this.getMenu().goForward())
            return true;

        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent keyEvent) {

        if (ReliableRecipeViewerClient.GO_BACK_RECIPE.matches(keyEvent) && this.getMenu().goBack())
            return true;

        if (ReliableRecipeViewerClient.GO_FORWARD_RECIPE.matches(keyEvent) && this.getMenu().goForward())
            return true;

		if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(keyEvent) && this.workstationSlot != null && this.workstationSlot.isHovered()) {
			ItemViewOverlay.INSTANCE.openRecipeView(this.workstationSlot.getStack(), ActionType.INPUT);
			return true;
		}

		if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(keyEvent) && this.workstationSlot != null && this.workstationSlot.isHovered()) {
			ItemViewOverlay.INSTANCE.openRecipeView(this.workstationSlot.getStack(), ActionType.RESULT);
			return true;
		}

        return super.keyPressed(keyEvent);
    }


    @Override
    protected void init() {
        super.init();

        this.prevRecipe = new ReliablePlainButton(Component.literal("<"), button -> this.getMenu().prevRecipe(button),
                12, 12);

        this.nextRecipe = new ReliablePlainButton(Component.literal(">"), button -> this.getMenu().nextRecipe(button),
                12, 12);

        this.prevTypePage = new ReliablePlainButton(Component.literal("<"), _ -> prevPage(),
                12, 14);

        this.nextTypePage = new ReliablePlainButton(Component.literal(">"), _ -> nextPage(),
                12, 14);

        this.checkGui();

        this.addRenderableWidget(this.prevRecipe);
        this.addRenderableWidget(this.nextRecipe);
        this.addRenderableWidget(this.prevTypePage);
        this.addRenderableWidget(this.nextTypePage);

        updateRecipeTypeButtons();
    }

    private void updateRecipeTypeButtons() {
        int size = 25;
        this.recipeTypeButtons.clear();
        for (int i = 0; i < this.getMenu().getViewTypeOrder().size(); i++) {
            int tempId = i % 5;

            int xPos = this.width / 2 - (64) + tempId * size + tempId * 2;
            int yPos = this.getTopPos() - size - 3;

            this.recipeTypeButtons.add(new RecipeTypeButton(this, xPos, yPos, size, size, this.getMenu().getViewTypeOrder().get(i), i));
        }
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRecipeWidget(T widget) {
        this.widgets.add(widget);
        return super.addRenderableWidget(widget);
    }

    public void clearRecipeWidgets() {
        this.widgets.forEach(r->{
            this.removeWidget((GuiEventListener) r);
        });
    }

    /// Switch to the previous type of recipes.
    public void prevPage() {
        this.viewTypePage = Math.max(this.viewTypePage - 1, 0);
        this.checkGui();
    }

    /// Switch to the next type of recipes
    public void nextPage() {
        this.viewTypePage = Math.min(this.viewTypePage + 1, this.getMenu().getViewTypeOrder().size() / 5);
        this.checkGui();
	}

    protected void checkGui() {
        this.clearRecipeWidgets();

        RecipeViewMenu menu = this.getMenu();
        boolean wrapScrolling = (Configs.CLIENT_SETTINGS.isWrapScrolling().equals(WrapScrolling.ON_BUTTONS) || Configs.CLIENT_SETTINGS.isWrapScrolling().equals(WrapScrolling.ENABLED)) && menu.getMaxPageIndex()>1;
        this.prevRecipe.active = menu.hasPrevRecipe() || wrapScrolling;
        this.nextRecipe.active = menu.hasNextRecipe() || wrapScrolling;

        this.prevTypePage.visible = this.viewTypePage > 0;
        this.nextTypePage.visible = this.viewTypePage < (menu.getViewTypeOrder().size() - 1) / 5;

        this.imageHeight = menu.getHeight();
        this.imageWidth = menu.getWidth();

        this.topPos = 32;
        if (Configs.CLIENT_SETTINGS.isCenterRecipeScreen())
            this.topPos += ((Minecraft.getInstance().getWindow().getGuiScaledHeight() - this.imageHeight) / 4);

        this.prevRecipe.setPosition(this.leftPos + 8, getTopPos() + 4);
        this.nextRecipe.setPosition(this.leftPos + this.imageWidth - 8 - 12, getTopPos() + 4);

        this.prevTypePage.setPosition(this.width / 2 - 64 - 16, getTopPos() - 18);
        this.nextTypePage.setPosition(this.width / 2 + 64 + 8, getTopPos() - 18);

        this.guiTitle = menu.getClientRecipeType().getDisplayName();
        this.titleLabelX = this.imageWidth / 2 - this.font.width(this.guiTitle) / 2;

        this.page = this.createPageComponent();

        this.animationTickCache.clear();
        this.checkTickers();


        //Transfer Button Logic
        this.transferButtons.forEach(this::removeWidget);
        this.transferButtons.clear();

        int guiLeft = this.leftPos + menu.guiOffsetLeft();

        for (int i = 0; i < menu.getCurrentDisplay().size(); i++) {
            final ReliableClientRecipe currentRecipe = menu.getCurrentDisplay().get(i);

            int guiTop = getTopPos() + menu.guiOffsetTop(i);

            int finalI = i;
            Button button = Button.builder(Component.literal("+"), button1 -> menu.quickCraft(currentRecipe, finalI))
                    .size(12, 12)
                    .pos(guiLeft + currentRecipe.getType().getDisplayWidth() + 4, guiTop + currentRecipe.getType().getDisplayHeight() / 2 - 6)
                    .build();

            RecipeTransferData data = menu.getTransferData().get(i);
            button.active = data.isSuccess() && currentRecipe.supportsItemTransfer() && RRVClientUtil.matchesAnyTransferClass(currentRecipe, menu.getParentScreen()) && currentRecipe.canTransferToScreen((AbstractContainerScreen<?>) menu.getParentScreen());
            button.visible = currentRecipe.supportsItemTransfer();

            this.addRenderableWidget(button);
            this.transferButtons.add(button);

        }

        updateRecipeTypeButtons();

    }

    private void checkTickers() {
        this.animationTickers.forEach(animationTicker -> {
            this.animationTickCache.put(animationTicker.id(), animationTicker.getTick());
        });

        this.animationTickers.clear();

        this.getMenu().getCurrentDisplay().forEach(recipe -> {
            recipe.getAnimationTickers().forEach(animationTicker -> {
                this.animationTickers.add(animationTicker);

                if (this.animationTickCache.containsKey(animationTicker.id()))
                    animationTicker.setTick(this.animationTickCache.get(animationTicker.id()));
                else
                    animationTicker.resetTick();
            });
        });
    }


    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.page, (this.imageWidth - font.width(this.page)) / 2, this.imageHeight - 12, -12566464, false);

        var color = isHoveringOverTitle(mouseX, mouseY) ? -5606651 : -12566464;
        guiGraphics.text(this.font, this.guiTitle, this.titleLabelX, this.titleLabelY, color, false);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isHoveringOverTitle(mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, List.of(this.guiTitle, Component.translatable("rrv.all_recipes_hint")), mouseX, mouseY);
        }
        // switch active workstation
        if (Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_FOOTER) && workstationSlot != null && !workstationSlot.isHovered()) {
            ClientLevel level = this.minecraft.level;
            if (level != null) {
                long gameTime = level.getGameTime();
                long l = gameTime % 40; // change every two seconds
                if (l == 0 && (gameTime-lastChanged > 40)) {
                    lastChanged = gameTime;
                    menu.setCurrentCraftReference(menu.getCurrentCraftReference() + 1);
                }
            }
        }
    }

    long lastChanged = 0;

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractContents(guiGraphics, mouseX, mouseY, a);
        List<ItemStack> craftReferences = getMenu().getCraftReferences();
        if (!craftReferences.isEmpty() && Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_FOOTER)) {
            var x = this.leftPos + 4;
            var y = this.imageHeight + 8;
            this.workstationSlot = new ItemSlot(craftReferences.get(menu.getCurrentCraftReference()), x, y);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("workstation_slot"),  x-1, y-1, 22, 22);
            this.workstationSlot.extractRenderState(guiGraphics, mouseX, mouseY, 0);
        }
    }

    @Override
    protected @NonNull List<Component> getTooltipFromContainerItem(@NonNull ItemStack itemStack) {
        List<Component> tooltip = super.getTooltipFromContainerItem(itemStack);

        Component component = ReliableRecipeViewerClient.addNamespaceTooltip(itemStack, tooltip, true);
        var index = component != null ? tooltip.indexOf(component) : tooltip.size();

        CompoundTag tagTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tagTag.contains(ReliableRecipeViewer.MOD_ID + "_itemTag")) {
            replaceTooltipWithTagDetails(tooltip, tagTag, "_itemTag", "tag.item.", index);
        }
        else if (tagTag.contains(ReliableRecipeViewer.MOD_ID + "_blockTag")) {
            replaceTooltipWithTagDetails(tooltip, tagTag, "_blockTag", "tag.block.", index);
        }

        if (Configs.CLIENT_SETTINGS.isShowRecipeId() && tagTag.contains(ReliableRecipeViewer.MOD_ID + "_result")) {
            String tagKeyString = tagTag.getStringOr(ReliableRecipeViewer.MOD_ID + "_result", "Error");
            MutableComponent tag = Component.literal(tagKeyString).withStyle(ChatFormatting.GRAY);
            tooltip.add(index, Component.translatable("view.rrv.recipe_id", tag).withStyle(ChatFormatting.GOLD));
        }

        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
            this.getMenu().getAdditionalStackModifier(this.hoveredSlot.getContainerSlot()).addTooltip(itemStack, tooltip);


        return tooltip;
    }

    private static void replaceTooltipWithTagDetails(List<Component> tooltip, CompoundTag nbt, String nbtPrefix, String languageKeyPrefix, int index) {
        Component first = tooltip.getFirst();
        String tagKeyString = nbt.getStringOr(ReliableRecipeViewer.MOD_ID + nbtPrefix, "Error");
		String baseTagTranslation = Identifier.parse(tagKeyString).toLanguageKey().replace("/", ".");
        String tagTranslation = languageKeyPrefix + baseTagTranslation;
        if (I18n.exists(tagTranslation)) {
            tooltip.addFirst(Component.translatable(tagTranslation));
        } else {
            tooltip.addFirst(Component.literal("#" + tagKeyString));
        }
        tooltip.set(1, Component.empty().append(Component.translatable("view.rrv.tags_displaying").withStyle(ChatFormatting.GOLD)).append(first));
        tooltip.add(index,
                Component.translatable("view.rrv.tags").append(": ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("#" + tagKeyString).withStyle(ChatFormatting.GRAY))

        );
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        if (Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_SIDEBAR)) {
            if (mouseX <= this.leftPos && mouseX >= this.leftPos - 25 && mouseY >= this.getTopPos() && mouseY <= this.getTopPos() + this.imageHeight) {
                if (scrollY < 0)
                    this.getMenu().nextReference();

                if (scrollY > 0)
                    this.getMenu().prevReference();

                return true;
            }
        } else if (Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_FOOTER) && workstationSlot != null && workstationSlot.isHovered()) {
            int max = menu.getCraftReferences().size() - 1;
            if (scrollY < 0)
                menu.setCurrentCraftReference(Mth.clamp(menu.getCurrentCraftReference()+1, 0, max));

            if (scrollY > 0)
                menu.setCurrentCraftReference(Mth.clamp(menu.getCurrentCraftReference()-1, 0, max));

            return true;
        }


        if (!(mouseX >= this.leftPos && mouseX <= this.leftPos + this.imageWidth && mouseY >= this.getTopPos() && mouseY <= this.getTopPos() + this.imageHeight))
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);


        if (scrollY < 0) {
            this.getMenu().nextRecipe(null);
            this.checkTickers();
        }
        if (scrollY > 0) {
            this.getMenu().prevRecipe(null);
            this.checkTickers();
        }

        if (scrollY != 0)
            this.page = this.createPageComponent();

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 1) {
            if (this.hoveredSlot != null) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.INPUT);
                return true;
            }
            else if (this.workstationSlot != null && this.workstationSlot.isHovered()) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.workstationSlot.getStack(), ActionType.INPUT);
                return true;
            }
        }

        if (mouseButtonEvent.button() == 0) {
            if (this.hoveredSlot != null) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.RESULT);
                return true;
            }
            else if (this.workstationSlot != null && this.workstationSlot.isHovered()) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.workstationSlot.getStack(), ActionType.RESULT);
                return true;
            }

            for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.recipeTypeButtons.size() > i; i++) {
                if (this.recipeTypeButtons.get(i).onClick(mouseButtonEvent))
                    return true;
            }

            if (isHoveringOverTitle(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.menu.getClientRecipeType());
            }

        }

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    private boolean isHoveringOverTitle(double mouseX, double mouseY) {
        int xMin = this.width / 2 - 64 - 2 - 3;
        int xMax = this.width / 2 + 64 + 2;
		return (mouseX > xMin && mouseX < xMax) && (mouseY >= this.getTopPos() && mouseY <= this.getTopPos() + 16);
	}

    private boolean isPrevTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos - 14 - 2 && mouseX <= this.leftPos - 2 && mouseY >= this.getTopPos() + 2 && mouseY <= this.getTopPos() + 2 + 14;
    }

    private boolean isNextTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos + this.imageWidth + 2 && mouseX <= this.leftPos + this.imageWidth + 2 + 14 && mouseY >= this.getLeftPos() + 2 && mouseY <= this.getTopPos() + 2 + 14;
    }

    @Override
    protected void containerTick() {
        this.animationTickers.forEach(AnimationTicker::tick);

        if (this.minecraft == null || this.minecraft.player == null)
            return;

        long timeOpen = (this.minecraft.player.level().getGameTime() - this.timestamp);

        if (timeOpen % 25 == 0 && timeOpen >= 25)
            this.getMenu().tickContents();

        this.getMenu().getCurrentDisplay().forEach(ReliableClientRecipe::tick);
    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public int getGuiWidth() {
        return this.imageWidth;
    }

    public int getGuiHeight() {
        return this.imageHeight;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);

        // render deselected icons
        renderRecipeTypeButtons(guiGraphics, mouseX, mouseY, partialTicks, false);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.getTopPos(), 0.0F, 0.0F, this.imageWidth, this.imageHeight - 3, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.getTopPos() + (this.imageHeight - 3), 0, 256 - 3, this.imageWidth, 3, 256, 256);


        ReliableClientRecipeType recipeType = this.getMenu().getClientRecipeType();

        //Render icons
        renderRecipeTypeButtons(guiGraphics, mouseX, mouseY, partialTicks, true);


        //Render craft references

        if (Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_SIDEBAR)) {
            for (int i = 0; i < this.getMenu().getDisplayableCraftReferences(); i++) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 25, this.getTopPos() + 4 + i * 24 + i, 231, 48, 25, 24, 256, 256);
            }

            if (this.getMenu().getCurrentCraftReference() > 0)
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.getTopPos() + 4 - 1 - 4, 248, 72, 8, 4, 256, 256);

            if (this.getMenu().getCurrentCraftReference() < menu.getCraftReferences().size() - this.getMenu().getDisplayableCraftReferences())
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.getTopPos() + 4 + (this.getMenu().getDisplayableCraftReferences()) * 25, 248, 76, 8, 4, 256, 256);
        }

        int guiLeft = this.leftPos + this.getMenu().guiOffsetLeft();

        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {

            int guiTop = this.getTopPos() + this.getMenu().guiOffsetTop(i);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(guiLeft, guiTop);

            if (recipeType.getGuiTexture() != null)
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, recipeType.getGuiTexture(), 0, 0, 0, 0, recipeType.getDisplayWidth(), recipeType.getDisplayHeight(), recipeType.getDisplayWidth(), recipeType.getDisplayHeight());

            //Optional slot rendering
            this.getMenu().slots.stream().filter(slot -> this.getMenu().isOptionalSlot(slot.index) && slot.hasItem()).forEach(slot -> {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(slot.x - (guiLeft - this.leftPos) - 1, slot.y - (guiTop - this.getTopPos()) - 1);
                this.getMenu().getOptionalSlotRenderer(slot.index).extractRenderState(guiGraphics, mouseX - guiLeft, mouseY - guiTop, partialTicks);
                guiGraphics.pose().popMatrix();
            });
            this.renderInvalidSlots(guiGraphics, i);
            this.getMenu().getCurrentDisplay().get(i).renderRecipe(this, new ReliableClientRecipe.RecipePosition(guiLeft, guiTop, recipeType.getDisplayWidth(), recipeType.getDisplayHeight()), guiGraphics, mouseX - guiLeft, mouseY - guiTop, partialTicks);
            guiGraphics.pose().popMatrix();
        }

    }

    private void renderRecipeTypeButtons(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, boolean b) {
        for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.recipeTypeButtons.size() > i; i++) {
            RecipeTypeButton recipeTypeButton = this.recipeTypeButtons.get(i);
            boolean selected = recipeTypeButton.recipeType() == this.getMenu().getClientRecipeType();
            if (selected == b)
                recipeTypeButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }


    private void renderInvalidSlots(GuiGraphicsExtractor guiGraphics, int displayId) {
        Button button = this.transferButtons.get(displayId);
        if (!button.isHovered())
            return;

        ReliableClientRecipe current = this.getMenu().getCurrentDisplay().get(displayId);

        RecipeTransferData data = this.getMenu().getTransferData().get(displayId);
        if (data.isSuccess())
            return;

        for (int slotId : data.getSlotResults().keySet()) {

            if (data.getSlotResults().get(slotId))
                continue;

            int actualSlotId = slotId + (displayId * current.getType().getSlotCount());
            Slot invSlot = this.getMenu().getSlot(actualSlotId);

            int x = invSlot.x;
            int y = invSlot.y;

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(-this.getMenu().guiOffsetLeft(), -this.getMenu().guiOffsetTop(displayId));
            guiGraphics.fill(x, y, x + 16, y + 16, new Color(255, 0, 0, 64).getRGB());
            guiGraphics.pose().popMatrix();

        }
    }


    record RecipeTypeButton(RecipeViewScreen viewScreen, int x, int y, int width, int height, ReliableClientRecipeType recipeType,
                            int viewTypeId) {

        private boolean onClick(MouseButtonEvent event) {
            return onClick(event.button(), (int) event.x(), (int) event.y());
        }

        private boolean onClick(int mouseButton, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return false;

            this.viewScreen.getMenu().setClientRecipeType(this.viewTypeId);
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        private void onHover(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return;

			ArrayList<Component> tooltip = new ArrayList<>(Collections.singleton(this.recipeType.getDisplayName()));
            if (Minecraft.getInstance().options.advancedItemTooltips || Configs.CLIENT_SETTINGS.isShowRecipeId()) {
                tooltip.add(Component.literal(this.recipeType.getId().toString()).withStyle(ChatFormatting.DARK_GRAY));
            }
            tooltip.add(Component.literal(Platform.INSTANCE.getModNameForNamespace(this.recipeType.getId().getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }

        private void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

//            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.x(), this.y(), 232, this.recipeType() == this.viewScreen.getMenu().getClientRecipeType() ? 24 : 0, 24, 24, 256, 256);

            boolean selected = this.recipeType() == this.viewScreen.getMenu().getClientRecipeType();
            Identifier sprite = selected ? SELECTED_TOP_TABS : UNSELECTED_TOP_TABS;
            var pos = 1;

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x(), y(), 26, 32);

            this.recipeType().renderIcon(this.viewScreen(), this.x()+5, this.y+8, guiGraphics, mouseX, mouseY, partialTicks);

            this.onHover(guiGraphics, mouseX, mouseY);
        }

    }
}

