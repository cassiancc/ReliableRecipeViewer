package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.network.payload.transfer.ServerboundTransferPayload;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeViewScreen extends AbstractContainerScreen<RecipeViewMenu> {

    private static final ResourceLocation VIEW_LOCATION = ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/recipe_view.png");

    //Timestamp when opening the view
    private final long timestamp;

    private Button prevRecipe, nextRecipe;
    private Component guiTitle, page;

    private final List<AnimationTicker> animationTickers;
    private final HashMap<ResourceLocation, Integer> animationTickCache;

    private final List<Button> transferButtons;

    //View Type
    private final List<ViewTypeButton> viewTypeButtons;
    private int viewTypePage;
    private Button prevTypePage, nextTypePage;

    public RecipeViewScreen(RecipeViewMenu recipeViewMenu, Inventory inventory, Component component) {
        super(recipeViewMenu, inventory, component);

        this.transferButtons = new ArrayList<>();
        this.viewTypeButtons = new ArrayList<>();
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {


        if (ReliableRecipeViewerClient.GO_BACK_RECIPE.matchesMouse(button) && this.getMenu().goBack())
            return true;
        if (ReliableRecipeViewerClient.GO_FORWARD_RECIPE.matchesMouse(button) && this.getMenu().goForward())
            return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (ReliableRecipeViewerClient.GO_BACK_RECIPE.matches(keyCode, scanCode) && this.getMenu().goBack())
            return true;

        if (ReliableRecipeViewerClient.GO_FORWARD_RECIPE.matches(keyCode, scanCode) && this.getMenu().goForward())
            return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    protected void init() {
        super.init();

        this.prevRecipe = Button.builder(Component.literal("<"), button -> {
                    this.getMenu().prevPage();
                })
                .size(12, 12)
                .build();

        this.nextRecipe = Button.builder(Component.literal(">"), button -> {
                    this.getMenu().nextRecipe();
                })
                .size(12, 12)
                .build();

        this.prevTypePage = Button.builder(Component.literal("<"), button -> {
                    this.viewTypePage = Math.max(this.viewTypePage - 1, 0);
                    this.checkGui();
                })
                .size(12, 12)
                .build();

        this.nextTypePage = Button.builder(Component.literal(">"), button -> {
                    this.viewTypePage = Math.min(this.viewTypePage + 1, this.getMenu().getViewTypeOrder().size() / 5);
                    this.checkGui();
                })
                .size(12, 12)
                .build();

        this.checkGui();

        this.addRenderableWidget(this.prevRecipe);
        this.addRenderableWidget(this.nextRecipe);
        this.addRenderableWidget(this.prevTypePage);
        this.addRenderableWidget(this.nextTypePage);

        int width = 24;
        int height = 24;

        this.viewTypeButtons.clear();
        for (int i = 0; i < this.getMenu().getViewTypeOrder().size(); i++) {
            int tempId = i % 5;

            int xPos = this.width / 2 - (5 * width / 2 + 4 * 2 / 2) + tempId * width + tempId * 2;
            int yPos = this.topPos - height - 1;

            this.viewTypeButtons.add(new ViewTypeButton(this, xPos, yPos, width, height, this.getMenu().getViewTypeOrder().get(i), i));
        }
    }


    protected void checkGui() {

        this.prevRecipe.active = this.getMenu().hasPrevRecipe();
        this.nextRecipe.active = this.getMenu().hasNextRecipe();

        this.prevTypePage.visible = this.viewTypePage > 0;
        this.nextTypePage.visible = this.viewTypePage < (this.getMenu().getViewTypeOrder().size() - 1) / 5;

        this.imageHeight = this.getMenu().getHeight();
        this.imageWidth = this.getMenu().getWidth();

        this.topPos = 32;

        this.prevRecipe.setPosition(this.leftPos + 8, this.topPos + 4);
        this.nextRecipe.setPosition(this.leftPos + this.imageWidth - 8 - 12, this.topPos + 4);

        this.prevTypePage.setPosition(this.width / 2 - (5 * 24 + 4 * 2) / 2 - 2 - 12, this.topPos - 1 - 12 - 6);
        this.nextTypePage.setPosition(this.width / 2 + (5 * 24 + 4 * 2) / 2 + 2, this.topPos - 1 - 12 - 6);

        this.guiTitle = this.getMenu().getClientRecipeType().getDisplayName();
        this.titleLabelX = this.imageWidth / 2 - this.font.width(this.guiTitle) / 2;

        this.page = this.createPageComponent();

        this.animationTickCache.clear();
        this.checkTickers();


        //Transfer Button Logic
        this.transferButtons.forEach(this::removeWidget);
        this.transferButtons.clear();

        int guiLeft = this.leftPos + this.getMenu().guiOffsetLeft();

        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {
            final ReliableClientRecipe currentView = this.getMenu().getCurrentDisplay().get(i);

            int guiTop = this.topPos + this.getMenu().guiOffsetTop(i);

            int finalI = i;
            Button button = Button.builder(Component.literal("+"), button1 -> {
                        if (!currentView.supportsItemTransfer())
                            return;

                        Minecraft.getInstance().setScreen(this.getMenu().getParentScreen());
                        LocalPlayer player = Minecraft.getInstance().player;

                        if (player != null && RrvUtil.matchesAnyTransferClass(currentView, Minecraft.getInstance().screen)) {

                            if (!currentView.canTransferToScreen((AbstractContainerScreen<?>) Minecraft.getInstance().screen))
                                return;

                            ReliableClientRecipe.RecipeTransferMap map = new ReliableClientRecipe.RecipeTransferMap();
                            currentView.mapRecipeItems(map, (AbstractContainerScreen<?>) Minecraft.getInstance().screen);


                            RecipeTransferData transferData = this.getMenu().getTransferData().get(finalI);

                            HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots = Screen.hasShiftDown() ? transferData.getStackedData().getUsedPlayerSlots() : transferData.getUsedPlayerSlots();
                            //TODO make component required in recipes
                            RrvClientNetworkManager.sendPacketToServer(new ServerboundTransferPayload(map.getTransferMap(), usedPlayerSlots));

                        }

                    })
                    .size(12, 12)
                    .pos(guiLeft + currentView.getViewType().getDisplayWidth() + 4, guiTop + currentView.getViewType().getDisplayHeight() / 2 - 6)
                    .build();

            RecipeTransferData data = this.getMenu().getTransferData().get(i);
            button.active = data.isSuccess() && currentView.supportsItemTransfer() && RrvUtil.matchesAnyTransferClass(currentView, this.getMenu().getParentScreen()) && currentView.canTransferToScreen((AbstractContainerScreen<?>) this.getMenu().getParentScreen());
            button.visible = currentView.supportsItemTransfer();

            this.addRenderableWidget(button);
            this.transferButtons.add(button);

        }

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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.page, (this.imageWidth - font.width(this.page)) / 2, this.imageHeight - 12, -12566464, false);
        if (isHoveringOverTitle(mouseX, mouseY)) {
            guiGraphics.drawString(this.font, this.guiTitle, this.titleLabelX, this.titleLabelY, -5606651, false); // colored title
        } else {
            guiGraphics.drawString(this.font, this.guiTitle, this.titleLabelX, this.titleLabelY, -12566464, false); // normal title
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        if (isHoveringOverTitle(mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, List.of(this.guiTitle, Component.translatable("rrv.all_recipes_hint")), mouseX, mouseY);
        }
    }


    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> tooltip = super.getTooltipFromContainerItem(itemStack);

        CompoundTag tagTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tagTag.contains(ReliableRecipeViewer.MOD_ID + "_recipeTag")) {
            Component first = tooltip.getFirst();
            String tagKeyString = tagTag.getStringOr(ReliableRecipeViewer.MOD_ID + "_recipeTag", "Error");
            var itemTagKeyTranslation = "tag.item."+ ResourceLocation.parse(tagKeyString).toLanguageKey().replace("/", ".");
            if (I18n.exists(itemTagKeyTranslation)) {
                tooltip.addFirst(Component.translatable(itemTagKeyTranslation));
            } else {
                tooltip.addFirst(Component.literal("#"+ tagKeyString));
            }
            tooltip.set(1, Component.empty().append(Component.translatable("view.rrv.tags_displaying").withStyle(ChatFormatting.GOLD)).append(first));
            tooltip.add(
                    Component.translatable("view.rrv.tags").append(": ").withStyle(ChatFormatting.GOLD)
                            .append(Component.literal("#" + tagKeyString).withStyle(ChatFormatting.GRAY))

            );
        }

        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
            this.getMenu().getAdditionalStackModifier(this.hoveredSlot.getContainerSlot()).addTooltip(itemStack, tooltip);

        //TODO make more performant
        if (Configs.CLIENT_SETTINGS.isAppendModNamespace())
            tooltip.addLast(Component.literal(ReliableRecipeViewerClient.resolver().getModNameForItem(itemStack)).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));

        return tooltip;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        if (mouseX <= this.leftPos && mouseX >= this.leftPos - 25 && mouseY >= this.topPos && mouseY <= this.topPos + this.imageHeight) {
            if (scrollY < 0)
                this.getMenu().nextReference();

            if (scrollY > 0)
                this.getMenu().prevReference();

            return true;
        }

        if (!(mouseX >= this.leftPos && mouseX <= this.leftPos + this.imageWidth && mouseY >= this.topPos && mouseY <= this.topPos + this.imageHeight))
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);


        if (scrollY < 0) {
            this.getMenu().nextPage();
            this.checkTickers();
        }
        if (scrollY > 0) {
            this.getMenu().prevPage();
            this.checkTickers();
        }

        if (scrollY != 0)
            this.page = this.createPageComponent();

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && this.hoveredSlot != null) {
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.INPUT);
            return true;
        }

        if (button == 0 && this.hoveredSlot != null) {
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.RESULT);
            return true;
        }

        if (button == 0) {

            for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.viewTypeButtons.size() > i; i++) {
                if (this.viewTypeButtons.get(i).onClick(button, (int) mouseX, (int) mouseY))
                    return true;
            }

            if (isHoveringOverTitle(mouseX, mouseY)) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.menu.getClientRecipeType());
            }

        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

	private boolean isHoveringOverTitle(double mouseX, double mouseY) {
        int xMin = this.width / 2 - 64 - 2 - 12;
        int xMax = this.width / 2 + 64 + 2;
		return (mouseX > xMin && mouseX < xMax) && (mouseY >= this.topPos && mouseY <= this.topPos + 16);
	}

    private boolean isPrevTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos - 14 - 2 && mouseX <= this.leftPos - 2 && mouseY >= this.topPos + 2 && mouseY <= this.topPos + 2 + 14;
    }

    private boolean isNextTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos + this.imageWidth + 2 && mouseX <= this.leftPos + this.imageWidth + 2 + 14 && mouseY >= this.topPos + 2 && mouseY <= this.topPos + 2 + 14;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {


        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight - 3, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.topPos + (this.imageHeight - 3), 0, 256 - 3, this.imageWidth, 3, 256, 256);


        ReliableClientRecipeType viewType = this.getMenu().getClientRecipeType();

        //Render icons

        int current = this.getMenu().getCurrentTypeIndex();

        for (int i = 0; i < 5; i++) {

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.width / 2 - (5 * 24 + 4 * 2) / 2 + i * 24 + i * 2, this.topPos - 24 - 1, 208, 0, 24, 24, 256, 256);
        }

        for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.viewTypeButtons.size() > i; i++) {
            this.viewTypeButtons.get(i).render(guiGraphics, mouseX, mouseY, partialTicks);
        }


        //Render craft references

        for (int i = 0; i < this.getMenu().getDisplayableCraftReferences(); i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 25, this.topPos + 4 + i * 24 + i, 231, 48, 25, 24, 256, 256);
        }

        if (this.getMenu().getCurrentCraftReference() > 0)
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.topPos + 4 - 1 - 4, 248, 72, 8, 4, 256, 256);

        if (this.getMenu().getCurrentCraftReference() < this.getMenu().getClientRecipeType().getCraftReferences().size() - this.getMenu().getDisplayableCraftReferences())
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.topPos + 4 + (this.getMenu().getDisplayableCraftReferences()) * 25, 248, 76, 8, 4, 256, 256);

        int guiLeft = this.leftPos + this.getMenu().guiOffsetLeft();

        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {

            int guiTop = this.topPos + this.getMenu().guiOffsetTop(i);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(guiLeft, guiTop);

            if (viewType.getGuiTexture() != null)
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, viewType.getGuiTexture(), 0, 0, 0, 0, viewType.getDisplayWidth(), viewType.getDisplayHeight(), viewType.getDisplayWidth(), viewType.getDisplayHeight());

            //Optional slot rendering
            this.getMenu().slots.stream().filter(slot -> this.getMenu().isOptionalSlot(slot.index) && slot.hasItem()).forEach(slot -> {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(slot.x - (guiLeft - this.leftPos) - 1, slot.y - (guiTop - this.topPos) - 1);
                this.getMenu().getOptionalSlotRenderer(slot.index).render(guiGraphics, mouseX - guiLeft, mouseY - guiTop, partialTicks);
                guiGraphics.pose().popMatrix();
            });
            this.renderInvalidSlots(guiGraphics, i);
            this.getMenu().getCurrentDisplay().get(i).renderRecipe(this, new ReliableClientRecipe.RecipePosition(guiLeft, guiTop, viewType.getDisplayWidth(), viewType.getDisplayHeight()), guiGraphics, mouseX - guiLeft, mouseY - guiTop, partialTicks);
            guiGraphics.pose().popMatrix();
        }

    }


    private void renderInvalidSlots(GuiGraphics guiGraphics, int displayId) {
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

            int actualSlotId = slotId + (displayId * current.getViewType().getSlotCount());
            Slot invSlot = this.getMenu().getSlot(actualSlotId);

            int x = invSlot.x;
            int y = invSlot.y;

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(-this.getMenu().guiOffsetLeft(), -this.getMenu().guiOffsetTop(displayId));
            guiGraphics.fill(x, y, x + 16, y + 16, new Color(255, 0, 0, 64).getRGB());
            guiGraphics.pose().popMatrix();

        }
    }


    record ViewTypeButton(RecipeViewScreen viewScreen, int x, int y, int width, int height, ReliableClientRecipeType viewType,
                          int viewTypeId) {


        private boolean onClick(int mouseButton, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return false;

            this.viewScreen.getMenu().setClientRecipeType(this.viewTypeId);
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        private void onHover(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return;

            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(this.viewType.getDisplayName()), mouseX, mouseY);
        }

        private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.x(), this.y(), 232, this.viewType() == this.viewScreen.getMenu().getClientRecipeType() ? 24 : 0, 24, 24, 256, 256);
            guiGraphics.renderFakeItem(this.viewType().getIcon(), this.x() + 4, this.y() + 4);

            this.onHover(guiGraphics, mouseX, mouseY);
        }

    }
}

