package cc.cassian.rrv.api;

import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

import java.util.Locale;

public enum ActionType implements StringRepresentable {
	INPUT(0),
	RESULT(1),
	ANY(2);

	public static Codec<ActionType> CODEC = StringRepresentable.fromEnum(ActionType::values);
	public static final StreamCodec<ByteBuf, ActionType> STREAM_CODEC = ByteBufCodecs.idMapper((ActionType::get), ActionType::getId);

	private final int id;

	ActionType(int id) {
		this.id = id;
	}

	private static synchronized ActionType get(int id) {
		ActionType[] values = values();
		for (ActionType type : values) {
			if (type.getId() == id) {
				return type;
			}
		}
		return ActionType.INPUT;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public int getId() {
		return id;
	}
}
