package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.client.sharing.RecipeSharing;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.client.util.RRVInputUtil;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.util.GuiWidgetAccess;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagClientRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.WorkstationDisplay;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.integration.ItemDescriptionsCompat;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.ReliableSpriteIconButton;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
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
import org.jspecify.annotations.Nullable;


import java.awt.*;
import java.util.*;
import java.util.List;

public class RecipeViewScreen extends Screen implements GuiWidgetAccess, RRVExtendedContainerScreen {

    private static final Identifier RECIPE_SCREEN = ReliableRecipeViewer.of("recipe_screen");
    private static final Identifier VIEW_LOCATION = ReliableRecipeViewer.of("textures/gui/recipe_view.png");
    private static final Identifier UNSELECTED_TOP_TABS = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2");

    private static final Identifier SELECTED_TOP_TABS = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2");

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    //Timestamp when opening the view
    private final long timestamp;

    private Button prevRecipe, nextRecipe;
    private Component guiTitle, page;

    private final List<AnimationTicker> animationTickers;
    private final HashMap<Identifier, Integer> animationTickCache;

    public final List<Button> transferButtons;
    public final List<Button> shareButtons;

    //Recipe Type
    private final List<RecipeTypeButton> recipeTypeButtons;
    private int viewTypePage;
    private Button prevTypePage, nextTypePage;
    private ItemSlot workstationSlot;

    // Screen Properties
    protected int imageWidth;
    protected int imageHeight;
    protected int titleLabelX;
    protected int titleLabelY;
    protected RecipeViewMenu menu;
    protected @Nullable Slot hoveredSlot;
    protected int leftPos;
    protected int topPos;

    public RecipeViewScreen(RecipeViewMenu recipeViewMenu, Inventory inventory, Component component) {
        super(component);
        this.menu = recipeViewMenu;

        this.transferButtons = new ArrayList<>();
        this.recipeTypeButtons = new ArrayList<>();
        this.shareButtons = new ArrayList<>();
        this.viewTypePage = 0;

        this.animationTickers = new ArrayList<>();
        this.animationTickCache = new HashMap<>();

        this.imageHeight = this.getMenu().getHeight();
        this.imageWidth = this.getMenu().getWidth();

        this.guiTitle = component;
        this.page = this.createPageComponent();
        this.titleLabelX = 8;
        this.titleLabelY = 6;

        this.timestamp = inventory.player.level().getGameTime();
        recipeViewMenu.setViewScreen(this);
    }


    private Component createPageComponent() {
        return Component.literal((this.getMenu().getCurrentPage() + 1) + "/" + (this.getMenu().getMaxPageIndex() + 1));
    }

    public RecipeViewMenu getMenu() {
        return this.menu;
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
        if (this.minecraft.options.keyInventory.matches(keyEvent)) {
            this.onClose();
            return true;
        }

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

        if (handleKeyPress(this, keyEvent)) {
            return true;
        }

        return super.keyPressed(keyEvent);
    }


    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.prevRecipe = new ReliablePlainButton(Component.literal("<"), button -> this.getMenu().prevRecipe(button),
                12, 12);

        this.nextRecipe = new ReliablePlainButton(Component.literal(">"), button -> this.getMenu().nextRecipe(button),
                12, 12);

        this.prevTypePage = new ReliablePlainButton(Component.literal("<"), button -> prevPage(),
                12, 14);

        this.nextTypePage = new ReliablePlainButton(Component.literal(">"), button -> nextPage(),
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

    /// Switch to the previous page of recipe types.
    public void prevPage() {
        this.viewTypePage = Math.max(this.viewTypePage - 1, 0);
        this.checkGui();
        this.getMenu().setClientRecipeType(this.viewTypePage*5);
    }

    /// Switch to the next page of recipe types.
    public void nextPage() {
        this.viewTypePage = Math.min(this.viewTypePage + 1, this.getMenu().getViewTypeOrder().size() / 5);
        this.checkGui();
        this.getMenu().setClientRecipeType(this.viewTypePage*5);
	}

    /// Switch to the previous type of recipes.
    public void prevRecipeType() {
        int typeId = this.getMenu().getCurrentTypeIndex();

        if (typeId-1 == -1) {
            return;
        }

        if (typeId % 5 == 0) {
            this.viewTypePage = Math.max(this.viewTypePage - 1, 0);
        }
        this.checkGui();
        this.getMenu().setClientRecipeType(typeId-1);
    }

    /// Switch to the next type of recipes
    public void nextRecipeType() {
        int typeId = this.getMenu().getCurrentTypeIndex() + 1;

        if (typeId>=this.getMenu().getViewTypeOrder().size())
            return;

        if (typeId != 0 && typeId % 5 == 0) {
            nextPage();
        } else {
            this.checkGui();
            this.getMenu().setClientRecipeType(typeId);
        }
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

        // Share Button Logic
        this.shareButtons.forEach(this::removeWidget);
        this.shareButtons.clear();

		int guiLeft = this.leftPos + menu.guiOffsetLeft();

		for (int i = 0; i < menu.getCurrentDisplay().size(); i++) {
			final ReliableClientRecipe currentRecipe = menu.getCurrentDisplay().get(i);
			ReliableClientRecipeType recipeType = currentRecipe.getType();
			int guiTop = getTopPos() + menu.guiOffsetTop(i);
			int finalI = i;
			RecipeViewMenu.DisplayInfo info = new RecipeViewMenu.DisplayInfo(guiLeft, guiTop, recipeType.getDisplayWidth(), recipeType.getDisplayHeight());
            currentRecipe.addRecipeWidgets(new RecipeScreenContext(this,this, this.font, new ReliableClientRecipe.RecipePosition(guiLeft, guiTop, recipeType.getDisplayWidth(), recipeType.getDisplayHeight()), null, (int) Minecraft.getInstance().mouseHandler.xpos(), (int) Minecraft.getInstance().mouseHandler.ypos(), 0));

            if (!ItemViewOverlay.INSTANCE.wasWarned()) {
                // transfer button
                var transferButtonData = recipeType.placeRecipeTransferButton(info);
                Button transferButton = new ReliablePlainButton(Component.literal("+"),
                        button1 -> menu.quickCraft(currentRecipe, finalI),
                        transferButtonData.x(), transferButtonData.y(),
                        12, 12);

                RecipeTransferData data = menu.getTransferData().get(i);
                transferButton.active = data.isSuccess() && currentRecipe.supportsItemTransfer() && RRVClientUtil.matchesAnyTransferClass(currentRecipe, menu.getParentScreen()) && currentRecipe.canTransferToScreen((AbstractContainerScreen<?>) menu.getParentScreen());
                transferButton.visible = currentRecipe.supportsItemTransfer() && transferButtonData.visible();

                this.addRenderableWidget(transferButton);
                this.transferButtons.add(transferButton);

                // share button
                var shareButtonData = recipeType.placeRecipeShareButton(info);
                Button shareButton = new ReliableSpriteIconButton(12,
                        Component.literal(">"),
                        12,
                        ReliableRecipeViewer.of("widget/share"),
                        button1 -> {
                            RecipeSharing.shareRecipe(currentRecipe);
                            RRVClientUtil.setScreen(this.getMenu().getParentScreen());
                        }
                );
                shareButton.setX(shareButtonData.x());
                shareButton.setY(shareButtonData.y());
                shareButton.setTooltip(Tooltip.create(Component.translatable("rrv.sharing.share", Component.literal(currentRecipe.entryId().toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD)));

                shareButton.active = true;
                shareButton.visible = shareButtonData.visible() && Configs.CLIENT_SETTINGS.isRecipeSharing();

                this.shareButtons.add(shareButton);
                this.addRenderableWidget(shareButton);

                if (currentRecipe instanceof ItemTagClientRecipe tagRecipe && Configs.STACK_GROUPS.areStackGroupsEnabled()) {
                    Identifier tagId = tagRecipe.getTagKey().location();
                    boolean exists = StackGroupManager.hasGroup(tagId);

                    Identifier base = ReliableRecipeViewer.of(exists ? "widget/tag_stack_group_enabled" : "widget/tag_stack_group_disabled");
                    Identifier hovered = ReliableRecipeViewer.of(exists ? "widget/tag_stack_group_enabled_highlighted" : "widget/tag_stack_group_disabled_highlighted");

                    Button tagGroupButton = new ReliableSpriteIconButton(12,
                            Component.empty(),
                            12,
                            base,
                            hovered,
                            button -> {
                                StackGroupManager.toggleTagGroup(tagId);
                                ItemViewOverlay.INSTANCE.updateDisplayedItems();
                                this.checkGui();
                            }
                    );
                    tagGroupButton.setX(shareButtonData.x() - 14);
                    tagGroupButton.setY(shareButtonData.y());
                    tagGroupButton.setTooltip(Tooltip.create(Component.translatable(exists ? "rrv.tag_recipe.stack_group.enabled" : "rrv.tag_recipe.stack_group.disabled").withStyle(ChatFormatting.GOLD)));

                    tagGroupButton.active = true;
                    tagGroupButton.visible = true;

                    this.addRenderableWidget(tagGroupButton);
                    GuiWidgetAccess.widgets.add(tagGroupButton);
                }
			}
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


    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.page, (this.imageWidth - font.width(this.page)) / 2, this.imageHeight - 12, -12566464, false);

        var color = isHoveringOverTitle(mouseX, mouseY) ? -5606651 : -12566464;
        guiGraphics.text(this.font, this.guiTitle, this.titleLabelX, this.titleLabelY, color, false);
    }

    //~ if >26 'render'->'extractRenderState' {
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    //~}
    //~ if >26 'render'->'extract'
        this.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isHoveringOverTitle(mouseX, mouseY)) {
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
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

    //~ if >26 'render' ->'extract'
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int xo = this.leftPos;
        int yo = this.topPos;
        //~ if >26 'render(' ->'extractRenderState('
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.pose().pushMatrix();
        graphics.pose().translate((float)xo, (float)yo);
        this.extractLabels(graphics, mouseX, mouseY);
        this.hoveredSlot = this.getHoveredSlot(mouseX, mouseY);
        this.extractSlotHighlightBack(graphics);
        this.extractSlots(graphics);
        this.extractSlotHighlightFront(graphics);

        graphics.pose().popMatrix();
        List<ItemStack> craftReferences = getMenu().getCraftReferences();
        if (!craftReferences.isEmpty() && Configs.CLIENT_SETTINGS.getWorkstationDisplay().equals(WorkstationDisplay.IN_FOOTER)) {
            var x = this.leftPos + 4;
            var y = this.topPos + this.imageHeight - 24;
            this.workstationSlot = new ItemSlot(craftReferences.get(menu.getCurrentCraftReference()), x, y, false);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("workstation_slot"),  x-1, y-1, 22, 22);
            this.workstationSlot.extractRenderState(graphics, mouseX, mouseY, 0);
        }
        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        RRVExtendedContainerScreen.extractOverlay(info, graphics, mouseX, mouseY, partialTicks);
    }

    protected @NonNull List<Component> getTooltipFromContainerItem(@NonNull ItemStack itemStack) {
        List<Component> tooltip = Screen.getTooltipFromItem(this.minecraft, itemStack);

        Component component = ReliableRecipeViewerClient.addNamespaceTooltip(itemStack, tooltip, false);

        CompoundTag customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (customData.contains(ReliableRecipeViewer.MOD_ID + "_itemTag")) {
            replaceTooltipWithTagDetails(tooltip, customData, "_itemTag", "tag.item.");
        }
        else if (customData.contains(ReliableRecipeViewer.MOD_ID + "_blockTag")) {
            replaceTooltipWithTagDetails(tooltip, customData, "_blockTag", "tag.block.");
        }

		Integer index = component != null ? tooltip.indexOf(component) : null;

		if (Configs.CLIENT_SETTINGS.isShowRecipeId() && customData.contains(ReliableRecipeViewer.MOD_ID + "_result")) {
            String tagKeyString = customData.getStringOr(ReliableRecipeViewer.MOD_ID + "_result", "Error");
            MutableComponent tag = Component.literal(tagKeyString).withStyle(ChatFormatting.GRAY);
            MutableComponent component1 = Component.translatable("view.rrv.recipe_id", tag).withStyle(ChatFormatting.GOLD);
            if (index == null) {
                tooltip.add(component1);
            } else {
                tooltip.set(index, component);
            }
        }

        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
            this.getMenu().getAdditionalStackModifier(this.hoveredSlot.getContainerSlot()).addTooltip(itemStack, tooltip);


        return tooltip;
    }

    private static void replaceTooltipWithTagDetails(List<Component> tooltip, CompoundTag nbt, String nbtPrefix, String languageKeyPrefix) {
        // clear tooltip and setup data
        Component itemName = tooltip.getFirst();
        tooltip.clear();
        String tagKeyString = nbt.getStringOr(ReliableRecipeViewer.MOD_ID + nbtPrefix, "Error");
        Identifier tagId = Identifier.parse(tagKeyString);
        String baseTagKey = tagId.toLanguageKey().replace("/", ".");
        String registryPrefixedTagKey = languageKeyPrefix + baseTagKey;
        String shortenedTagKey = "tag." + baseTagKey;

        // add tag name, ideally translated
        Component name;
        if (RrvUtil.has(registryPrefixedTagKey)) {
            name = Component.translatable(registryPrefixedTagKey);
        } else if (RrvUtil.has(shortenedTagKey)) {
            name = Component.translatable(shortenedTagKey);
        } else {
            name = Component.literal("#" + tagKeyString);
        }
        tooltip.add(name);


        // add tag description
        if (ModCompat.ITEM_DESCRIPTIONS)
            ItemDescriptionsCompat.addTagDescription(tooltip, baseTagKey, name);

        // "Displaying: "
        tooltip.add(Component.empty().append(Component.translatable("view.rrv.tags_displaying").withStyle(ChatFormatting.GOLD)).append(itemName));

        // "Accepts any: "
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(
                    Component.translatable("view.rrv.tags").append(": ").withStyle(ChatFormatting.GOLD)
                            .append(Component.literal("#" + tagKeyString).withStyle(ChatFormatting.GRAY))
            );
        }

        // Add tag namespace
        ReliableRecipeViewerClient.addNamespaceTooltip(RRVPlatform.INSTANCE.getModNameForNamespace(tagId.getNamespace()), tooltip, true);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrollX, scrollY))
            return true;

        // scroll through workstations
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

        // scroll through recipe types
        int topPos1 = this.getTopPos() + 16;
        if (mouseY < topPos1 && scrollY < 0) {
            this.nextRecipeType();
            return true;
        }
        if (mouseY < topPos1 && scrollY > 0) {
            this.prevRecipeType();
            return true;
        }

        if (!(mouseX >= this.leftPos && mouseX <= this.leftPos + this.imageWidth && mouseY >= this.getTopPos() && mouseY <= this.getTopPos() + this.imageHeight))
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);


        // scroll through recipes
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
        if (RRVInputUtil.isRightClick(mouseButtonEvent)) {
            if (this.hoveredSlot != null) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.INPUT);
                return true;
            }
            else if (this.workstationSlot != null && this.workstationSlot.isHovered()) {
                ItemViewOverlay.INSTANCE.openRecipeView(this.workstationSlot.getStack(), ActionType.INPUT);
                return true;
            }
        }

        if (RRVInputUtil.isLeftClick(mouseButtonEvent)) {
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

        return super.mouseClicked(mouseButtonEvent, bl)  | OverlayManager.INSTANCE.mouseClicked(mouseButtonEvent, bl);
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
    public final void tick() {
        super.tick();
        if (this.minecraft.player != null && this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
            this.animationTickers.forEach(AnimationTicker::tick);

            long timeOpen = (this.minecraft.player.level().getGameTime() - this.timestamp);

            if (timeOpen % 25 == 0 && timeOpen >= 25)
                this.getMenu().tickContents();

            this.getMenu().getCurrentDisplay().forEach(ReliableClientRecipe::tick);
        } else {
            this.minecraft.player.closeContainer();
        }
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
    public final void rrv$callInit() {
        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                RRVClientUtil.CONTAINER,
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));
        OverlayManager.INSTANCE.setCurrentInvInfo(info);
        RRVExtendedContainerScreen.updateWidgets(this);
    }

    protected void extractSlots(final GuiGraphicsExtractor graphics) {
        for (Slot slot : this.menu.slots) {
            if (slot.isActive()) {
                this.extractSlot(graphics, slot);
            }
        }
    }

    protected void extractTooltip(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack item = this.hoveredSlot.getItem();
            graphics.setTooltipForNextFrame(this.font, this.getTooltipFromContainerItem(item), item.getTooltipImage(), mouseX, mouseY, item.get(DataComponents.TOOLTIP_STYLE));
        }
    }

    protected void extractSlot(final GuiGraphicsExtractor guiGraphics, final Slot slot) {
        int x = slot.x;
        int y = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean done = false;

        if (itemStack.isEmpty() && slot.isActive()) {
            Identifier icon = slot.getNoItemIcon();
            if (icon != null) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x, y, 16, 16);
                done = true;
            }
        }

        if (!done) {
            int seed = slot.x + slot.y * this.imageWidth;
            guiGraphics.fakeItem(itemStack, x, y, seed);
            guiGraphics.itemDecorations(this.font, itemStack, x, y, null);
        }

    }

    @Nullable Slot getHoveredSlot(final double x, final double y) {
        for (Slot slot : this.menu.slots) {
            if (slot.isActive() && this.isHovering(slot, x, y)) {
                return slot;
            }
        }

        return null;
    }

    private boolean isHovering(final Slot slot, final double xm, final double ym) {
        return this.isHovering(slot.x, slot.y, 16, 16, xm, ym);
    }

    protected boolean isHovering(final int left, final int top, final int width, final int height, double mouseX, double mouseY) {
        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        return mouseX >= (double)(left - 1) && mouseX < (double)(left + width + 1) && mouseY >= (double)(top - 1) && mouseY < (double)(top + height + 1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);

        // render deselected icons
        renderRecipeTypeButtons(guiGraphics, mouseX, mouseY, partialTicks, false);

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, RECIPE_SCREEN, leftPos, topPos, imageWidth, imageHeight+topPos-30, -1);


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

            if (this.getMenu().getCurrentCraftReference() < getMenu().getCraftReferences().size() - this.getMenu().getDisplayableCraftReferences())
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
            this.getMenu().getCurrentDisplay().get(i).renderRecipe(new RecipeScreenContext(this,this, this.font, new ReliableClientRecipe.RecipePosition(guiLeft, guiTop, recipeType.getDisplayWidth(), recipeType.getDisplayHeight()), guiGraphics, mouseX, mouseY, partialTicks));
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
        if (this.transferButtons.isEmpty()) return;
        Button button = this.transferButtons.get(displayId);
        if (!button.isHovered()) return;

        ReliableClientRecipe current = this.getMenu().getCurrentDisplay().get(displayId);

        RecipeTransferData data = this.getMenu().getTransferData().get(displayId);
        if (data.isSuccess()) return;

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

    @Override
    public ItemStack rrv$hoveredStack() {
        return hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
    }

    @Override
    public boolean rrv$triggerInitLater() {
        return false;
    }

    //Optional Slots

    public void extractSlotHighlightBack(GuiGraphicsExtractor graphics) {
        if (!checkOptionalSlot()) {
            if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            }
		}
    }

    public void extractSlotHighlightFront(GuiGraphicsExtractor graphics) {
        if (!checkOptionalSlot()) {
            if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            }
		}
    }

    private boolean checkOptionalSlot() {
        return this.hoveredSlot != null && !this.hoveredSlot.hasItem() && getMenu().isOptionalSlot(this.hoveredSlot.index);
    }

    @Override
    public void onClose() {
        RRVExtendedContainerScreen.clearOverlay();
        RRVClientUtil.setScreen(getMenu().getParentScreen());
    }

	public void setMenu(RecipeViewMenu recipeViewMenu) {
		this.menu = recipeViewMenu;
        checkGui();
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
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
            tooltip.add(Component.literal(RRVPlatform.INSTANCE.getModNameForNamespace(this.recipeType.getId().getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }

        private void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

            boolean selected = this.recipeType() == this.viewScreen.getMenu().getClientRecipeType();
            Identifier sprite = selected ? SELECTED_TOP_TABS : UNSELECTED_TOP_TABS;

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x(), y(), 26, 32);

            this.recipeType().renderIcon(this.viewScreen(), this.x()+5, this.y+8, guiGraphics, mouseX, mouseY, partialTicks);

            this.onHover(guiGraphics, mouseX, mouseY);
        }

    }
}

