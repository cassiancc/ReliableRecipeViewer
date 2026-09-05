package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.util.MobFood;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.ItemDescriptionsCompat;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.rendering.RrvGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cc.cassian.rrv.common.recipe.ItemViewRecipes.MOB_FOOD;

public class EntityClientRecipe implements ReliableClientRecipe {
    private static final Identifier FOOD_SLOT_TEXTURE = ReliableRecipeViewer.of("textures/gui/food_slot.png");
    private static final Identifier FOOD_EMPTY = Identifier.withDefaultNamespace("hud/food_empty");
    private static final Identifier FOOD_FULL = Identifier.withDefaultNamespace("hud/food_full");
    private final EntityType<?> entityType;
    private final Identifier entityId;
    private final List<SlotContent> drops;
    private final Identifier fieldGuideSprite;
    private final boolean hasFieldGuideSprite;

    private LivingEntity previewEntity;

    private int animationTick = 0;
    private boolean hovered = false;
    private boolean renderingFood = false;

    public EntityClientRecipe(EntityServerRecipe serverRecipe) {
        this.entityType = serverRecipe.getEntityType();
        this.entityId = entityType.builtInRegistryHolder().key().identifier();

        List<SlotContent> drops = serverRecipe.getDrops();
        List<SlotContent> dropContents = new ArrayList<>();

        for (int i = 0; i < this.getType().getSlotCount(); i++) {
            if (drops.size() > i)
                dropContents.add(drops.get(i));
            else
                dropContents.add(SlotContent.of());
        }

        this.drops = dropContents;

        this.fieldGuideSprite = entityId.withPath("textures/fieldguide/entries/%s.png"::formatted);
        this.hasFieldGuideSprite = Minecraft.getInstance().getResourceManager().getResource(fieldGuideSprite).isPresent();
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return EntityClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        if (previewEntity instanceof Animal animal) {
            if (!MOB_FOOD.containsKey(entityType)) {
                List<ItemStack> food = new ArrayList<>();
                for (ItemStack itemStack : ItemFilters.fullStackList()) {
                    if (animal.isFood(itemStack)) food.add(itemStack);
                }
                MOB_FOOD.put(entityType, new MobFood(SlotContent.of(food)));
            }
        }

        if (MOB_FOOD.containsKey(entityType)) {
            MobFood mobFood = MOB_FOOD.get(entityType);
            SlotContent slotContent = mobFood.slotContent();
            if (!slotContent.isEmpty()) {
                slotFillContext.bindSlot(0, slotContent);
                mobFood.lore().ifPresent(lore-> slotFillContext.addAdditionalStackModifier(0, lore));
                renderingFood = true;
            }
        } else {
            renderingFood = false;
        }

        for (int i = 0; i < this.drops.size(); i++) {
            slotFillContext.bindSlot(i+1, this.drops.get(i));
        }

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of();
    }

    @Override
    public List<SlotContent> getResults() {
        return this.drops;
    }

    @Override
    public Identifier getId() {
        return entityId.withPrefix("/entity/");
    }

    @Override
    public void tick() {

        if (this.hovered)
            return;

        this.animationTick++;
        if (this.animationTick >= 360)
            this.animationTick = 0;
    }

    @Override
    public void initRecipe() {

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        Entity entity = this.getEntityType().create(level, EntitySpawnReason.LOAD);
        if (entity instanceof LivingEntity livingEntity) {
            this.previewEntity = livingEntity;
            this.previewEntity.setYBodyRot(30.0F);
            this.previewEntity.setYHeadRot(30.0F);
        }
    }

    @Override
    public void fadeRecipe() {
        if (this.previewEntity != null)
            this.previewEntity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {

        Component entityName = this.entityType.getDescription();

        this.renderEntity(context);

		this.hovered = context.mouseX() >= 5 && context.mouseX() < 5 + 64 && context.mouseY() >= 3 && context.mouseY() < 3 + 64;

        int xo = context.recipePosition().left() + context.mouseX();
        int yo = context.recipePosition().top() + context.mouseY();
        if (this.hovered) {
            var tooltip = new  ArrayList<Component>();
			ChatFormatting style;
            if (previewEntity instanceof NeutralMob) style = ChatFormatting.YELLOW;
            else if (entityType.getCategory().isFriendly()) style = ChatFormatting.GREEN;
			else style = ChatFormatting.RED;
			tooltip.add(Component.empty().append(entityName).withStyle(style));
            if (ModCompat.ITEM_DESCRIPTIONS)
                ItemDescriptionsCompat.addEntityDescription(tooltip, entityType, entityName);
            ReliableRecipeViewerClient.addNamespaceTooltip(RRVPlatform.INSTANCE.getModNameForNamespace(entityId.getNamespace()), tooltip, true);
            context.guiGraphics().setComponentTooltipForNextFrame(context.font(), tooltip, xo, yo);
        }
        if (renderingFood) {
            int x = 16;
            int y = 84;
            context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, FOOD_SLOT_TEXTURE, 9, y-5, 0, 0, 58, 18, 58, 18);
            context.guiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY, x, y, 9, 9);
            context.guiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_FULL, x, y, 9, 9);

		    if (context.screen() instanceof RRVExtendedContainerScreen recipeViewScreen && !recipeViewScreen.rrv$hoveredStack().isEmpty())
			    return;

            if (context.mouseX() >= 9 && context.mouseX() < 9 + 58 && context.mouseY() >= 79 && context.mouseY() < 79 + 18) {
                context.guiGraphics().setTooltipForNextFrame(context.font(), Tooltip.create(Component.translatable("view.rrv.type.entity.food")).toCharSequence(Minecraft.getInstance()), xo, yo);
            }

        }
        if (context.mouseX() >= 72 && context.mouseX() < 72 + 30 && context.mouseY() >= 19 && context.mouseY() < 19 + 30) {
            context.guiGraphics().setTooltipForNextFrame(context.font(), Tooltip.create(Component.translatable("view.rrv.type.entity.drops")).toCharSequence(Minecraft.getInstance()), xo, yo);
        }

    }

    private void renderEntity(RecipeScreenContext context) {
        int x = 9;
        int y = 7;

        if (hasFieldGuideSprite) {
            context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, fieldGuideSprite, x, y, 0, 0, 55, 55, 55, 55);
        } else {
            if (this.previewEntity == null)
                return;
            float scale = 26.0F;

            AABB boundingBox = this.previewEntity.getBoundingBox();
            if (boundingBox.getYsize() * scale > 38)
                scale = (float) (38.0F / boundingBox.getYsize());

            RrvGuiRenderHelper.renderEntityOnScreen(
                    context.guiGraphics(),
                    this.previewEntity,
                    context.recipePosition().left() + x,
                    context.recipePosition().top() + y,
                    context.recipePosition().left() + x + 56,
                    context.recipePosition().top() + y + 56,
                    scale, new Vector3f(0.0F, (50F / scale / 2.0F), 0.0F),
                    new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), (this.animationTick + context.partialTicks()) / 180.0F * Mth.PI, 0.0F), null);

        }


    }

    @Override
    public boolean isVisualOnly() {
        return true;
    }
}
