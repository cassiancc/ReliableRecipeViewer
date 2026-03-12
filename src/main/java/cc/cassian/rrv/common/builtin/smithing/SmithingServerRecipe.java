package cc.cassian.rrv.common.builtin.smithing;

import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.mojang.datafixers.util.Pair;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jspecify.annotations.Nullable;

public class SmithingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<SmithingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("smithing"),
            () -> new SmithingServerRecipe(null, false, null, null, null, null, null)
    );

	private final Identifier id;
	private boolean isTrim;
    private Ingredient base, template, addition;
    private TrimPattern pattern;
    private ItemStackTemplate upgradeResult;


    public SmithingServerRecipe(Identifier id, boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, TrimPattern pattern, @Nullable ItemStackTemplate upgradeResult) {
		this.id = id;
		this.isTrim = isTrim;
        this.base = base;
        this.template = template;
        this.addition = addition;
        this.pattern = pattern;
        this.upgradeResult = upgradeResult;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public boolean isTrim() {
        return this.isTrim;
    }

    public Ingredient getBase() {
        return this.base;
    }

    public Ingredient getTemplate() {
        return this.template;
    }

    public Ingredient getAddition() {
        return this.addition;
    }

    public TrimPattern getPattern() {
        return this.pattern;
    }

    @Nullable
    public ItemStackTemplate getUpgradeResult() {
        return this.upgradeResult;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", TagUtil.writeIngredient(this.base));
        tag.put("template", TagUtil.writeIngredient(this.template));
        tag.put("addition", TagUtil.writeIngredient(this.addition));

        if(this.pattern != null)
            tag.put("pattern", TrimPattern.DIRECT_CODEC.encode(this.pattern, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());

        if(this.upgradeResult != null) {
            tag.put("upgradeResult", ItemStackTemplate.CODEC.encode(this.upgradeResult, ServerRecipeManager.INSTANCE.createSerializationContext(), new CompoundTag()).getOrThrow());
		}
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.isTrim = tag.getBooleanOr("isTrim", false);
        this.base = TagUtil.readIngredient(tag.getCompound("base").orElseGet(CompoundTag::new));
        this.template = TagUtil.readIngredient(tag.getCompound("template").orElseGet(CompoundTag::new));
        this.addition = TagUtil.readIngredient(tag.getCompound("addition").orElseGet(CompoundTag::new));

        this.pattern = TrimPattern.DIRECT_CODEC.decode(NbtOps.INSTANCE, tag.getCompound("pattern").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);
        this.upgradeResult = ItemStackTemplate.CODEC.decode(ClientRecipeManager.INSTANCE.createSerializationContext(), tag.getCompound("upgradeResult").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
