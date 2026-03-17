package cc.cassian.rrv.common.builtin.smithing;

import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
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
    private SlotContent base, template, addition, upgradeResult;
    private TrimPattern pattern;


    public SmithingServerRecipe(Identifier id, boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, TrimPattern pattern, @Nullable ItemStackTemplate upgradeResult) {
		this.id = id;
		this.isTrim = isTrim;
        this.base = SlotContent.of(base);
        this.template = SlotContent.of(template);
        this.addition = SlotContent.of(addition);
        this.pattern = pattern;
        this.upgradeResult = SlotContent.of(upgradeResult);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public boolean isTrim() {
        return this.isTrim;
    }

    public SlotContent getBase() {
        return this.base;
    }

    public SlotContent getTemplate() {
        return this.template;
    }

    public SlotContent getAddition() {
        return this.addition;
    }

    public TrimPattern getPattern() {
        return this.pattern;
    }

    @Nullable
    public SlotContent getUpgradeResult() {
        return this.upgradeResult;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", TagUtil.writeSlotContent(this.base));
        tag.put("template", TagUtil.writeSlotContent(this.template));
        tag.put("addition", TagUtil.writeSlotContent(this.addition));

        if(this.pattern != null)
            tag.put("pattern", TrimPattern.DIRECT_CODEC.encode(this.pattern, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());

        if(!this.upgradeResult.isEmpty()) {
            tag.put("upgradeResult", TagUtil.writeSlotContent(this.upgradeResult));
		}
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.isTrim = tag.getBooleanOr("isTrim", false);
        this.base = TagUtil.readSlotContent(tag.getCompound("base").orElseGet(CompoundTag::new));
        this.template = TagUtil.readSlotContent(tag.getCompound("template").orElseGet(CompoundTag::new));
        this.addition = TagUtil.readSlotContent(tag.getCompound("addition").orElseGet(CompoundTag::new));

        this.pattern = TrimPattern.DIRECT_CODEC.decode(NbtOps.INSTANCE, tag.getCompound("pattern").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);
        this.upgradeResult = TagUtil.readSlotContent(tag.getCompound("upgradeResult").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
