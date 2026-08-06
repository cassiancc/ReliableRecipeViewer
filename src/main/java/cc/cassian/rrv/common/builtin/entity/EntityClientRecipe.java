package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.rendering.RrvGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EntityClientRecipe implements ReliableClientRecipe {

    private final EntityType<?> entityType;
    private final List<SlotContent> drops;

    private LivingEntity previewEntity;

    private int animationTick = 0;
    private boolean hovered = false;

    public EntityClientRecipe(EntityServerRecipe serverRecipe) {
        this.entityType = serverRecipe.getEntityType();

        List<SlotContent> drops = serverRecipe.getDrops();
        List<SlotContent> dropContents = new ArrayList<>();

        for (int i = 0; i < this.getType().getSlotCount(); i++) {
            if (drops.size() > i)
                dropContents.add(drops.get(i));
            else
                dropContents.add(SlotContent.of());
        }

        this.drops = dropContents;
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

        for (int i = 0; i < this.drops.size(); i++) {
            if (i < 9)
                slotFillContext.bindSlot(i, this.drops.get(i));
            else
                slotFillContext.bindOptionalSlot(i, this.drops.get(i), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
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
        return this.entityType.builtInRegistryHolder().key().identifier();
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

		this.hovered = context.mouseX() >= 65 && context.mouseX() <= 65 + 32 && context.mouseY() >= 0 && context.mouseY() <= 32;

        int xo = context.recipePosition().left() + context.mouseX();
        int yo = context.recipePosition().top() + context.mouseY();
        if (this.hovered) {
            context.guiGraphics().setTooltipForNextFrame(context.font(), Component.empty().append(entityName).withStyle(ChatFormatting.GOLD), xo, yo);
        }
    }

    private void renderEntity(RecipeScreenContext context) {

        if (this.previewEntity == null)
            return;

        float scale = 12.0F;

        AABB boundingBox = this.previewEntity.getBoundingBox();
        if (boundingBox.getYsize() * scale > 26)
            scale = (float) (26.0F / boundingBox.getYsize());

        RrvGuiRenderHelper.renderEntityOnScreen(context.guiGraphics(), this.previewEntity, context.recipePosition().left() + 67, context.recipePosition().top() + 2, context.recipePosition().left() + 67 + 28, context.recipePosition().top() + 2 + 28, scale, new Vector3f(0.0F, (28.0F / scale / 2.0F), 0.0F), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), (this.animationTick + context.partialTicks()) / 180.0F * Mth.PI, 0.0F), null);

    }

    @Override
    public boolean isVisualOnly() {
        return true;
    }
}
