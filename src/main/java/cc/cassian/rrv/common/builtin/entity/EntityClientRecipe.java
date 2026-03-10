package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.rendering.RrvGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
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

        List<ItemStack> drops = serverRecipe.getDrops();
        List<SlotContent> dropContents = new ArrayList<>();

        for (int i = 0; i < this.getViewType().getSlotCount(); i++) {
            if (drops.size() > i)
                dropContents.add(SlotContent.of(drops.get(i)));
            else
                dropContents.add(SlotContent.of());
        }

        this.drops = dropContents;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @Override
    public ReliableClientRecipeType getViewType() {
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
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Component entityName = this.entityType.getDescription();

        this.renderEntity(screen, recipePosition, guiGraphics, mouseX, mouseY, partialTicks);

        if (mouseX >= 65 && mouseX <= 65 + 32 && mouseY >= 0 && mouseY <= 32)
            this.hovered = true;
        else
            this.hovered = false;

        if (this.hovered)
            guiGraphics.setComponentTooltipForNextFrame(screen.getFont(), List.of(Component.empty().append(entityName).withStyle(ChatFormatting.GOLD)), recipePosition.left() + mouseX, recipePosition.top() + mouseY);

    }

    private void renderEntity(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

        if (this.previewEntity == null)
            return;

        float scale = 12.0F;

        AABB boundingBox = this.previewEntity.getBoundingBox();
        if (boundingBox.getYsize() * scale > 26)
            scale = (float) (26.0F / boundingBox.getYsize());

        RrvGuiRenderHelper.renderEntityOnScreen(guiGraphics, this.previewEntity, recipePosition.left() + 67, recipePosition.top() + 2, recipePosition.left() + 67 + 28, recipePosition.top() + 2 + 28, scale, new Vector3f(0.0F, (28.0F / scale / 2.0F), 0.0F), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), (this.animationTick + partialTicks) / 180.0F * Mth.PI, 0.0F), null);

    }

    @Override
    public boolean isVisualOnly() {
        return true;
    }
}
