package de.crafty.eiv.common.recipe.rendering;

import net.minecraft.resources.Identifier;

public class AnimationTicker {

    private final int duration;
    private int tick;
    private final Identifier id;


    private AnimationTicker(Identifier id, int duration) {
        this.duration = duration;
        this.id = id;
    }

    public Identifier id() {
        return this.id;
    }

    /**
     *
     * @return The current tick of this ticker
     */
    public int getTick() {
        return this.tick;
    }

    public void tick() {
        this.tick++;
        if (this.tick >= this.duration)
            this.tick = 0;
    }

    /**
     * Overwrite the current tick-status of this ticker
     *
     * @param tick The new tick
     */
    public void setTick(int tick) {
        this.tick = tick;
    }

    /**
     * Reset the ticker back to the start
     */
    public void resetTick(){
        this.tick = 0;
    }

    /**
     *
     * @return The current progress
     */
    public float getProgress() {
        return (float) this.tick / (float) (this.duration - 1);
    }

    /**
     *
     * @return The total duration
     */
    public int getDuration() {
        return this.duration;
    }

    /**
     *
     * @param id The id for this ticker (use the same id for same durations)
     * @param duration The time this ticker needs to finish one loop (in ticks)
     */
    public static AnimationTicker create(Identifier id, int duration) {
        return new AnimationTicker(id, duration);
    }
}
