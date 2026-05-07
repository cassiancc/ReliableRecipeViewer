package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.config.Configs;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TutorialState implements StringRepresentable {
    ALWAYS,
    WITH_KEYBIND,
    NEVER;

    public static boolean showTutorial() {
        return Configs.CLIENT_SETTINGS.getTutorialState().equals(TutorialState.ALWAYS) ||(Configs.CLIENT_SETTINGS.getTutorialState().equals(TutorialState.WITH_KEYBIND) && Minecraft.getInstance().hasControlDown());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<TutorialState> CODEC = StringRepresentable.fromEnum(TutorialState::values);
}
