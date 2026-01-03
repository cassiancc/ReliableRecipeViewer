package de.crafty.eiv.common.network.payload.transfer;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public record ServerboundTransferPayload(HashMap<Integer, Integer> transferMap,
                                         HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) implements CustomPacketPayload {


    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTransferPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            ServerboundTransferPayload::encodeMap,
            ServerboundTransferPayload::decodeMap

    );

    public static final Type<ServerboundTransferPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_transfer"));


    private CompoundTag encodeMap() {
        CompoundTag encoded = new CompoundTag();

        CompoundTag transferMap = new CompoundTag();
        this.transferMap.forEach((recipeSlot, destSlot) -> {
            transferMap.putInt(String.valueOf(recipeSlot), destSlot);
        });
        encoded.put("transferMap", transferMap);

        CompoundTag usedPlayerSlots = new CompoundTag();
        this.usedPlayerSlots.forEach((recipeSlot, usedSlots) -> {

            CompoundTag playerSlotsTag = new CompoundTag();
            usedSlots.forEach((playerSlot, stack) -> {
                playerSlotsTag.put(String.valueOf(playerSlot), EivTagUtil.encodeItemStackOnClient(stack));
            });

            usedPlayerSlots.put(String.valueOf(recipeSlot), playerSlotsTag);
        });

        encoded.put("usedPlayerSlots", usedPlayerSlots);
        return encoded;
    }

    private static ServerboundTransferPayload decodeMap(CompoundTag encoded) {

        HashMap<Integer, Integer> transferMap = new HashMap<>();
        CompoundTag encodedTransferMap = encoded.getCompound("transferMap").orElseGet(CompoundTag::new);

        encodedTransferMap.keySet().forEach(recipeSlot -> {
            transferMap.put(Integer.valueOf(recipeSlot), encodedTransferMap.getInt(recipeSlot).orElse(Integer.valueOf(recipeSlot)));
        });

        HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots = new HashMap<>();
        CompoundTag encodedUsedPlayerSlots = encoded.getCompound("usedPlayerSlots").orElseGet(CompoundTag::new);

        encodedUsedPlayerSlots.keySet().forEach(recipeSlot -> {
            HashMap<Integer, ItemStack> usedSlots = new HashMap<>();

            CompoundTag playerSlotsTag = encodedUsedPlayerSlots.getCompound(recipeSlot).orElseGet(CompoundTag::new);
            playerSlotsTag.keySet().forEach(playerSlot -> {

                ItemStack stack = EivTagUtil.decodeItemStackOnServer(playerSlotsTag.getCompound(playerSlot).orElseGet(CompoundTag::new));
                usedSlots.put(Integer.valueOf(playerSlot), stack);
            });

            usedPlayerSlots.put(Integer.valueOf(recipeSlot), usedSlots);
        });

        return new ServerboundTransferPayload(transferMap, usedPlayerSlots);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
