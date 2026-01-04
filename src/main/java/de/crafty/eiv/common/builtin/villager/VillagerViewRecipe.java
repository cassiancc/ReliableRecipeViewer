package de.crafty.eiv.common.builtin.villager;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.rendering.EivGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class VillagerViewRecipe implements IEivViewRecipe {


    private final SlotContent offer, cost1, cost2;

    protected VillagerServerRecipe.VillagerOffer villagerOffer;

    private Villager previewVillager = null;
    private boolean prevVillagerLookLeft, villagerLookLeft = false;
    private int lastHeadChange, currentTick = 0;

    private final Random random;

    public VillagerViewRecipe(VillagerServerRecipe.VillagerOffer villagerOffer) {

        this.offer = SlotContent.of(villagerOffer.offerStacks());
        this.cost1 = SlotContent.of(villagerOffer.cost1());
        this.cost2 = SlotContent.of(villagerOffer.cost2());

        this.villagerOffer = villagerOffer;
        this.random = new Random();


        if (Minecraft.getInstance().level != null)
            this.prevVillagerLookLeft = this.villagerLookLeft = this.random.nextBoolean();
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return VillagerViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindDepedantSlot(0, this.offer::index, this.cost1);
        slotFillContext.bindSlot(1, this.cost2);

        slotFillContext.bindSlot(2, this.offer);

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.cost1, this.cost2);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.offer);
    }


    @Override
    public void tick() {

        this.currentTick++;

        if (Minecraft.getInstance().level == null)
            return;

        if (this.currentTick - this.lastHeadChange < 3 * 20)
            return;

        this.prevVillagerLookLeft = this.villagerLookLeft;
        this.villagerLookLeft = this.random.nextBoolean();
        this.lastHeadChange = this.currentTick;

    }

    @Override
    public void initRecipe() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        this.previewVillager = EntityType.VILLAGER.create(level, EntitySpawnReason.LOAD);
        if (this.previewVillager == null)
            return;


        this.previewVillager.setVillagerData(this.previewVillager.getVillagerData().withLevel(this.villagerOffer.professionLevel()).withType(level.registryAccess(), VillagerType.PLAINS).withProfession(level.registryAccess(), this.villagerOffer.profession()));
        this.previewVillager.setNoAi(true);
        this.previewVillager.setYHeadRot((this.villagerLookLeft ? -1.0F : 1.0F) * 15.0F);

        if (this.villagerOffer.requiredtype() != null)
            this.previewVillager.setVillagerData(this.previewVillager.getVillagerData().withType(level.registryAccess().lookupOrThrow(Registries.VILLAGER_TYPE).getOrThrow(this.villagerOffer.requiredtype())));

    }

    @Override
    public void fadeRecipe() {
        if (this.previewVillager != null)
            this.previewVillager.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Font font = Minecraft.getInstance().font;

        ResourceKey<VillagerProfession> profession = this.villagerOffer.profession();
        String namespace = profession.identifier().getNamespace();
        String path = profession.identifier().getPath();
        float scale = 0.75F;

        Component professionComp = Component.translatable("entity." + namespace + ".villager." + path).append(" - ").append(Component.translatable("merchant.level." + this.villagerOffer.professionLevel())).withStyle(ChatFormatting.DARK_GRAY);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, -(font.lineHeight) * scale);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.drawString(font, professionComp, 0, 0, -1, false);
        guiGraphics.pose().popMatrix();

        if (this.villagerLookLeft != this.prevVillagerLookLeft && this.currentTick - this.lastHeadChange <= 0.25F * 20) {
            float pastTime = this.currentTick - this.lastHeadChange + partialTicks;
            float headRotationProgress = pastTime / (0.25F * 20.0F);
            this.previewVillager.setYHeadRot((this.villagerLookLeft ? -1.0F : 1.0F) * (15.0F * headRotationProgress));
        }

        this.renderVillager(guiGraphics, recipePosition, mouseX, mouseY, partialTicks);

        if (this.villagerOffer.requiredtype() == null)
            return;

        if (mouseX >= 0 && mouseX <= 24 && mouseY >= 0 && mouseY <= 36) {
            Identifier typeLocation = this.villagerOffer.requiredtype().identifier();
            Component typeComponent = Component.translatable("view.eiv.type.trading." + typeLocation.getNamespace() + "." + typeLocation.getPath()).withStyle(ChatFormatting.GOLD);
            guiGraphics.setComponentTooltipForNextFrame(font, List.of(typeComponent), recipePosition.left() + mouseX, recipePosition.top() + mouseY);
        }
    }


    private void renderVillager(GuiGraphics guiGraphics, RecipePosition recipePosition, int mouseX, int mouseY, float partialTicks) {

        if (this.previewVillager == null)
            return;

        EivGuiRenderHelper.renderEntityOnScreen(guiGraphics, this.previewVillager, recipePosition.left() + 2, recipePosition.top() + 2, recipePosition.left() + 2 + 20, recipePosition.top() + 2 + 32, 15.0F, new Vector3f(0, (32.0F / 15.0F / 2.0F), 0), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), 0.0F, 0.0F), null);

    }
}
