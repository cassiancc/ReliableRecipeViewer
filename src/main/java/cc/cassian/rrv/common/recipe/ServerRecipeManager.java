package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.recipe.*;
import cc.cassian.rrv.common.network.payload.reload.ClientboundServerReloadPayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStackSensitivePayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

//TODO block incoming requests while update sending
public class ServerRecipeManager {

    public static final ServerRecipeManager INSTANCE = new ServerRecipeManager();

    private static final HashMap<ReliableServerRecipeType<?>, List<ServerRecipeEntry>> PRESENT_RECIPES = new LinkedHashMap<>();

    private MinecraftServer server;
    private RecipeManager recipeManager;

    private ServerRecipeManager() {

    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        this.reload();
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void setRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public RecipeManager getVanillaRecipeManager() {
        return this.recipeManager;
    }

    //Helper functionality
    public <T extends Recipe<?>> List<T> getRecipesForType(RecipeType<T> recipeType) {
        return (List<T>) this.recipeManager.getRecipes().stream().filter(holder -> holder.value().getType().equals(recipeType)).map(RecipeHolder::value).toList();
    }

    public RegistryOps<Tag> createSerializationContext() {
        return server.theGame().registryAccess().createSerializationContext(NbtOps.INSTANCE);
    }

    public void reload() {
        if (this.server == null || this.server.theGame() == null || this.recipeManager == null)
            return;


        ReliableRecipeViewer.LOGGER.info("Reloading all Recipes...");

        ItemView.getStackSensitive().clear();
        ItemView.getReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
        this.server.theGame().playerList().getPlayers().forEach(player -> {
            RrvNetworkManager.INSTANCE.sendPacket(player, new ClientboundServerReloadPayload());
        });

        this.broadcastStackSensitives();

        this.reloadRecipes();
        this.broadcastAllRecipes();

    }


    public void broadcastStackSensitives() {

        if (this.server == null)
            return;

        ReliableRecipeViewer.LOGGER.info("Broadcasting Stack-Sensitives...");
        ReliableRecipeViewer.LOGGER.info("Informing {} players about {} stack-sensitives", this.server.theGame().playerList().getPlayers().size(), ItemView.getStackSensitive().size());
        this.server.theGame().playerList().getPlayers().forEach(this::updateStackSensitives);


    }

    public void updateStackSensitives(ServerPlayer player) {
        List<ItemView.StackSensitive> collected = new ArrayList<>();
        ItemView.getStackSensitive().forEach((item, stackSensitives) -> {
            collected.addAll(stackSensitives);
        });

        ReliableRecipeViewer.networkManager().sendPacket(player, new ClientboundStartStackSensitivesPayload(collected.size()));
        collected.forEach(stackSensitive -> {
            ReliableRecipeViewer.networkManager().sendPacket(player, new ClientboundStackSensitivePayload(stackSensitive));
        });
        ReliableRecipeViewer.networkManager().sendPacket(player, new ClientboundFinishStackSensitivesPayload());
    }

    public void broadcastAllRecipes() {
        if (this.server == null) {
            return;
        }
        ReliableRecipeViewer.LOGGER.info("Broadcasting recipes...");
        this.server.theGame().playerList().getPlayers().forEach(this::informAboutRecipes);
    }


    public void informAboutRecipes(ServerPlayer serverPlayer) {
        if (PRESENT_RECIPES.isEmpty())
            return;

        ReliableRecipeViewer.LOGGER.info("Informing {} about {} recipe types", serverPlayer.getName(), PRESENT_RECIPES.size());

        ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundStartUpdatesPayload());

        ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundCacheStartPayload(PRESENT_RECIPES.size()));
        PRESENT_RECIPES.forEach((type, entries) -> {
            ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdateStartPayload(type, entries.size()));
            entries.forEach(recipe -> {
                ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdatePayload(recipe));
            });
            ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdateEndPayload(type));
        });
        ReliableRecipeViewer.networkManager().sendPacket(serverPlayer, new ClientboundFinishUpdatesPayload());

    }

    public void reloadRecipes() {
        PRESENT_RECIPES.clear();

        List<ReliableServerRecipe> serverRecipes = new ArrayList<>();
        ItemViewRecipes.INSTANCE.getRecipeProviders().forEach(serverModRecipeProvider -> {
            List<ReliableServerRecipe> recipes = new ArrayList<>();
            serverModRecipeProvider.provide(recipes);
            serverRecipes.addAll(recipes);
        });

        serverRecipes.forEach(iRrvServerModRecipe -> {

            ResourceLocation typeId = iRrvServerModRecipe.getRecipeType().getId();
            List<ServerRecipeEntry> list = PRESENT_RECIPES.getOrDefault(iRrvServerModRecipe.getRecipeType(), new ArrayList<>());
            list.add(new ServerRecipeEntry(ResourceLocation.fromNamespaceAndPath(typeId.getNamespace(), typeId.getPath() + "/" + UUID.randomUUID()), iRrvServerModRecipe));
            PRESENT_RECIPES.put(iRrvServerModRecipe.getRecipeType(), list);
        });
    }


    public record ServerRecipeEntry(ResourceLocation modRecipeId, ReliableServerRecipe recipe) {

        public static final StreamCodec<FriendlyByteBuf, ServerRecipeEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                entry -> entry.modRecipeId().toString(),
                ByteBufCodecs.COMPOUND_TAG,
                ServerRecipeEntry::createFullTag,
                (s, compoundTag) -> new ServerRecipeEntry(ResourceLocation.tryParse(s), ServerRecipeEntry.fromTag(compoundTag))
        );

        public <T extends ReliableServerRecipe> T asWrapped() {
            return (T) this.recipe;
        }

        private CompoundTag createFullTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("recipeType", this.recipe().getRecipeType().getId().toString());
            try {
                CompoundTag dataTag = new CompoundTag();
                this.recipe().writeToTag(dataTag);
                tag.put("recipeData", dataTag);
            }catch (Exception e) {
                ReliableRecipeViewer.LOGGER.error("Failed to encode recipe {}: {}, please contact the mod author", this.modRecipeId(), e.getMessage());
            }

            return tag;
        }

        public static ReliableServerRecipe fromTag(CompoundTag tag) {
            if (!tag.contains("recipeType"))
                return null;

            ReliableServerRecipeType<?> recipeType = ReliableServerRecipeType.byId(ResourceLocation.parse(tag.getString("recipeType").orElseThrow()));
            if (recipeType == null)
                return null;

            ReliableServerRecipe modRecipe = recipeType.getEmptyConstructor().construct();
            modRecipe.loadFromTag(tag.getCompound("recipeData").orElseGet(CompoundTag::new));
            return modRecipe;
        }
    }


    /**
     * @param player          The player
     * @param transferMap     The transfer map
     * @param usedPlayerSlots The player slots
     * @return Returns whether the current crafting container recipe is the same as the recipe quick-crafted by the player
     */
    private boolean hasSameRecipeInsideContainer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

        for (int recipeSlotId : transferMap.keySet()) {
            int destSlotId = transferMap.get(recipeSlotId);

            Slot destSlot = player.containerMenu.getSlot(destSlotId);

            ItemStack destStack = destSlot.getItem();
            ItemStack requiredStack = usedPlayerSlots.getOrDefault(recipeSlotId, new HashMap<>()).values().stream().findFirst().orElse(ItemStack.EMPTY);

            if (!destStack.isEmpty() && (destStack.getCount() >= destStack.getMaxStackSize() || !ItemStack.isSameItemSameComponents(destStack, requiredStack)))
                return false;

        }

        return true;
    }


    //Transfer
    public void performRecipeTransfer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

        List<ItemStack> stacks = new ArrayList<>();

        //Only accepts filled crafting containers when there is the same recipe inside
        //Otherwise: Clear the content to avoid conflicts
        if (!this.hasSameRecipeInsideContainer(player, transferMap, usedPlayerSlots)) {
            player.containerMenu.slots.forEach(slot -> {
                if (!slot.getItem().isEmpty() && transferMap.containsValue(slot.index))
                    stacks.add(slot.remove(slot.getItem().getCount()));
            });
        }


        //Actual item transfer
        transferMap.forEach((recipeSlot, destSlotId) -> {

            HashMap<Integer, ItemStack> usedSlots = usedPlayerSlots.getOrDefault(recipeSlot, new HashMap<>());
            Slot destSlot = player.containerMenu.getSlot(destSlotId);

            usedSlots.forEach((playerSlot, stack) -> {
                ItemStack currentInDest = destSlot.getItem();

                if (currentInDest.isEmpty()) {
                    destSlot.set(player.containerMenu.getSlot(playerSlot).remove(stack.getCount()));
                } else {
                    destSlot.set(currentInDest.copyWithCount(currentInDest.getCount() + player.containerMenu.getSlot(playerSlot).remove(Math.min(stack.getCount(), currentInDest.getMaxStackSize() - currentInDest.getCount())).getCount()));
                }

            });


        });

        //Add cached items back to inventory
        stacks.forEach(player::addItem);

    }

}
