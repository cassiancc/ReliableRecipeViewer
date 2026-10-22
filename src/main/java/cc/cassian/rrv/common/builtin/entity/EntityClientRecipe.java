package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.util.MobFood;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.integration.ItemDescriptionsCompat;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ReliableSpriteIconButton;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.rendering.RrvGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cc.cassian.rrv.common.recipe.ItemViewRecipes.MOB_FOOD;

public class EntityClientRecipe implements ReliableClientRecipe {
    private static final Identifier WIDE_SLOT_TEXTURE = ReliableRecipeViewer.of("textures/gui/wide_slot.png");
    private static final Identifier FOOD_EMPTY = Identifier.withDefaultNamespace("hud/food_empty");
    private static final Identifier FOOD_FULL = Identifier.withDefaultNamespace("hud/food_full");
    private static final Identifier DROPS_SLOT = ReliableRecipeViewer.of("entity_drops_slot");
    private static final Identifier LEFT_ARROW = ReliableRecipeViewer.of("arrow/left");
    private static final Identifier LEFT_ARROW_DISABLED = ReliableRecipeViewer.of("arrow/left_disabled");
    private static final Identifier RIGHT_ARROW = ReliableRecipeViewer.of("arrow/right");
    private static final Identifier RIGHT_ARROW_DISABLED = ReliableRecipeViewer.of("arrow/right_disabled");
    private static final Rect2i FOOD_BOUNDING_BOX = new Rect2i(9, 79, 58, 18);
    private static final Rect2i DROPS_BOUNDING_BOX = new Rect2i(72, 19, 30, 30);
    private static final Rect2i ENTITY_BOUNDING_BOX = new Rect2i(5, 3, 64, 64);
    private static final Rect2i BIOME_BOUNDING_BOX = new Rect2i(9, 97, 58, 18);
    private static final Rect2i BIOME_BOUNDING_BOX_ALT = new Rect2i(9, 97, 58, 18);
    private final EntityType<?> entityType;
    private final Identifier entityId;
    private final List<SlotContent> drops;
    private final Identifier fieldGuideSprite;
    private final boolean hasFieldGuideSprite;
    private final int dropsRows;
    private final int dropsPages;
    private final ReliableSpriteIconButton nextPageButton;
    private final ReliableSpriteIconButton prevPageButton;
    private final ArrayList<Identifier> biomes;

    private LivingEntity previewEntity;

    private int currentDropsPage = 0;
    private int animationTick = 0;
    private int biomeTick = 0;
    private boolean hovered = false;
    private boolean renderingFood = false;
    private int biomeIndex = 0;
    public static Identifier UNDEFINED = Identifier.fromNamespaceAndPath("immersiveoverlays", "textures/immersiveoverlays/undefined.png");

    public EntityClientRecipe(EntityServerRecipe serverRecipe) {
        this.entityType = serverRecipe.getEntityType();
        this.entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        this.drops = new ArrayList<>(serverRecipe.getDrops());

        this.dropsRows = Math.clamp(Math.ceilDiv(drops.size(), 3), 3, 7);

        for (int i = 0; i < this.drops.size() % 21; i++) {
            drops.add(SlotContent.of());
        }

        this.dropsPages = Math.ceilDiv(this.drops.size(), 21);

        this.prevPageButton = new ReliableSpriteIconButton(12, Component.translatable("rrv.previous_page"), 8, LEFT_ARROW, LEFT_ARROW, LEFT_ARROW_DISABLED, _ -> onPageChangeButtonPressed(-1));
        this.prevPageButton.active = false;
        this.prevPageButton.visible = dropsPages > 1;

        this.nextPageButton = new ReliableSpriteIconButton(12, Component.translatable("rrv.next_page"), 8, RIGHT_ARROW, RIGHT_ARROW, RIGHT_ARROW_DISABLED, _ -> onPageChangeButtonPressed(1));
        this.nextPageButton.active = true;
        this.nextPageButton.visible = dropsPages > 1;

        this.fieldGuideSprite = entityId.withPath("textures/fieldguide/entries/%s.png"::formatted);
        this.hasFieldGuideSprite = Minecraft.getInstance().getResourceManager().getResource(fieldGuideSprite).isPresent();
        this.biomes = new ArrayList<>();
        if (RRVPlatform.INSTANCE.isModLoaded("immersiveoverlays")) {
            for (Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).entrySet()) {
                Biome biome = biomeEntry.getValue();
                biome.getMobSettings().getMobs(entityType.getCategory()).unwrap().forEach(spawnerDataWeighted -> {
                    if (spawnerDataWeighted.value().type().equals(entityType)) {
                        biomes.add(biomeEntry.getKey().identifier());
                    }
                });
            }
        }
    }

    private void onPageChangeButtonPressed(final int offset) {
        this.currentDropsPage += offset;
        this.nextPageButton.active = this.currentDropsPage < this.dropsPages - 1;
        this.prevPageButton.active = this.currentDropsPage >= 1;

        if (RRVClientUtil.currentScreen() instanceof RecipeViewScreen recipeViewScreen) recipeViewScreen.getMenu().updateByPage();
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
            slotFillContext.bindSlot(i + 1, this.drops.get((i + this.currentDropsPage * 21) % this.drops.size()));
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
        this.biomeTick++;
        if (this.biomeTick > 32 && !this.biomes.isEmpty()) {
			if (this.biomes.size()-1 > this.biomeIndex + 1) {
				this.biomeIndex++;
			} else {
				this.biomeIndex = 0;
			}
			this.biomeTick = 0;
        }
    }

    @Override
    public void initRecipe() {

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        //? if >26.1 {
        /*Entity entity = this.getEntityType().create(level, new EntitySpawnRequest(EntitySpawnReason.LOAD, true));
         *///?} else {
        Entity entity = this.getEntityType().create(level, EntitySpawnReason.LOAD);
        //?}
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
    public void addRecipeWidgets(RecipeScreenContext context) {
        this.prevPageButton.setPosition(context.recipePosition().left() + 107, context.recipePosition().top() + 130);
        context.widgets().addRecipeWidget(prevPageButton);

        this.nextPageButton.setPosition(context.recipePosition().left() + 145, context.recipePosition().top() + 130);
        context.widgets().addRecipeWidget(nextPageButton);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Component entityName = this.entityType.getDescription();

        context.guiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, DROPS_SLOT, 105, 3, 54, 18 * dropsRows);

        if (this.dropsPages > 1) {
            final Font font = Minecraft.getInstance().font;
            final String text = this.currentDropsPage + 1 + "/" + this.dropsPages;
            context.guiGraphics().text(font, text, 132 - font.width(text) / 2, 136 - font.lineHeight / 2, 0xFF404040, false);
        }

        this.renderEntity(context);

        this.hovered = ENTITY_BOUNDING_BOX.contains(context.mouseX(), context.mouseY());

        if (this.hovered) {
            var tooltip = new  ArrayList<Component>();
			ChatFormatting style;
            if (previewEntity instanceof NeutralMob) style = ChatFormatting.YELLOW;
            else if (entityType.getCategory().isFriendly()) style = ChatFormatting.GREEN;
			else style = ChatFormatting.RED;
			tooltip.add(Component.empty().append(entityName).withStyle(style));
            if (!entityType.isAllowedInPeaceful() && Minecraft.getInstance().level.getDifficulty().equals(Difficulty.PEACEFUL)) {
                tooltip.add(Component.translatable("item.spawn_egg.peaceful").withStyle(ChatFormatting.RED));
            }
            if (ModCompat.ITEM_DESCRIPTIONS)
                ItemDescriptionsCompat.addEntityDescription(tooltip, entityType, entityName);
            ReliableRecipeViewerClient.addNamespaceTooltip(RRVPlatform.INSTANCE.getModNameForNamespace(entityId.getNamespace()), tooltip, true);
            context.setTooltip(tooltip);
        }
        renderFood(context);
        context.setTooltip(Tooltip.create(Component.translatable("view.rrv.type.entity.drops")), DROPS_BOUNDING_BOX);
        renderBiomes(context);

    }

    private void renderBiomes(RecipeScreenContext context) {
		if (biomes.isEmpty()) {
			return;
		}
        var box = renderingFood ? BIOME_BOUNDING_BOX : BIOME_BOUNDING_BOX_ALT;

        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, WIDE_SLOT_TEXTURE, box.getX(), box.getY(), 0, 0, box.getWidth(), box.getHeight(), box.getWidth(), box.getHeight());
        context.guiGraphics().fakeItem(Items.RED_BED.getDefaultInstance(), box.getX()+5, box.getY()+1);
		var id = getBiomeSprite(biomes.get(biomeIndex), true);
        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, id, box.getX()+34, box.getY()+1, 0, 0, 16, 16, 16, 16);
        context.setTooltip(List.of(
                Component.translatable("view.rrv.type.entity.spawns"),
                Component.translatable("view.rrv.type.entity.spawns_item", Component.translatable(biomes.get(biomeIndex).toLanguageKey("biome")).withStyle(ChatFormatting.GRAY))
        ), box);
	}

    private void renderFood(RecipeScreenContext context) {
		if (!renderingFood) {
			return;
		}
		int x = 16;
		int y = 84;
		context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, WIDE_SLOT_TEXTURE, 9, y-5, 0, 0, 58, 18, 58, 18);
		context.guiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY, x, y, 9, 9);
		context.guiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_FULL, x, y, 9, 9);

		if (context.screen() instanceof RRVExtendedContainerScreen recipeViewScreen && !recipeViewScreen.rrv$hoveredStack().isEmpty()) {
			return;
		}

		context.setTooltip(Tooltip.create(Component.translatable("view.rrv.type.entity.food")), FOOD_BOUNDING_BOX);
	}

    public static Identifier getBiomeSprite(Identifier biome, boolean allowRedirect) {
        var manager = Minecraft.getInstance().getResourceManager();
        var path = "textures/immersiveoverlays/" + biome.getPath();
        var key = Identifier.fromNamespaceAndPath(biome.getNamespace(), "%s.png".formatted(path));
        var redirect = Identifier.fromNamespaceAndPath(biome.getNamespace(), "%s.txt".formatted(path));
        if (manager.getResource(key).isPresent())
            return key;
        else {
            if (manager.getResource(redirect).isPresent() && allowRedirect) {
                try {
                    return getBiomeSprite(Objects.requireNonNull(Identifier.tryParse(manager.openAsReader(redirect).lines().toList().getFirst())), false);
                } catch (Exception e) {
                    return UNDEFINED;
                }
            } else {
                return UNDEFINED;
            }
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
