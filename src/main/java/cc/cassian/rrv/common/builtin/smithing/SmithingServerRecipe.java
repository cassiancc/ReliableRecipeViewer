package cc.cassian.rrv.common.builtin.smithing;

import com.mojang.datafixers.util.Pair;
import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

public class SmithingServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<SmithingServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("smithing"),
            () -> new SmithingServerRecipe(false, null, null, null, null, null)
    );

    private boolean isTrim;
    private Ingredient base, template, addition;
    private TrimPattern pattern;

    private TransmuteResult upgradeResult;

    public SmithingServerRecipe(boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, TrimPattern pattern, @Nullable TransmuteResult upgradeResult) {
        this.isTrim = isTrim;
        this.base = base;
        this.template = template;
        this.addition = addition;

        this.pattern = pattern;
        this.upgradeResult = upgradeResult;
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
    public TransmuteResult getUpgradeResult() {
        return this.upgradeResult;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", RrvTagUtil.writeIngredient(this.base));
        tag.put("template", RrvTagUtil.writeIngredient(this.template));
        tag.put("addition", RrvTagUtil.writeIngredient(this.addition));

        if(this.pattern != null)
            tag.put("pattern", TrimPattern.DIRECT_CODEC.encode(this.pattern, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());

        if(this.upgradeResult != null)
            tag.put("upgradeResult", TransmuteResult.CODEC.encode(this.upgradeResult, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.isTrim = tag.getBooleanOr("isTrim", false);
        this.base = RrvTagUtil.readIngredient(tag.getCompound("base").orElseGet(CompoundTag::new));
        this.template = RrvTagUtil.readIngredient(tag.getCompound("template").orElseGet(CompoundTag::new));
        this.addition = RrvTagUtil.readIngredient(tag.getCompound("addition").orElseGet(CompoundTag::new));

        this.pattern = TrimPattern.DIRECT_CODEC.decode(NbtOps.INSTANCE, tag.getCompound("pattern").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);
        this.upgradeResult = TransmuteResult.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("upgradeResult").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
