package com.moulberry.flashback.playback;

import com.moulberry.flashback.Flashback;
import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.Entity;

public class TickRateManager {
    public static final float MIN_TICKRATE = 1.0F;
    private float tickrate = 20.0F;
    private long nanosecondsPerTick = TimeUtil.NANOSECONDS_PER_SECOND / 20L;
    private boolean runGameElements = true;
    private boolean isFrozen = false;
    private boolean isServerTickRateManager = false;

    public TickRateManager(boolean isServerTickRateManager) {
        this.isServerTickRateManager = isServerTickRateManager;
    }

    public void setTickRate(float tickRate) {
        this.tickrate = Math.max(tickRate, MIN_TICKRATE);
        this.nanosecondsPerTick = (long)((double)TimeUtil.NANOSECONDS_PER_SECOND / (double)this.tickrate);
    }

    public float tickrate() {
        return this.tickrate;
    }

    public float millisecondsPerTick() {
        return (float)this.nanosecondsPerTick / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND;
    }

    public long nanosecondsPerTick() {
        return this.nanosecondsPerTick;
    }

    public boolean runsNormally() {
        return this.runGameElements;
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    public void tick() {
        this.runGameElements = !this.isFrozen && !this.isServerTickRateManager;
    }

    public boolean isEntityFrozen(Entity entity) {
        if (this.isServerTickRateManager) {
            return !(entity instanceof ReplayPlayer);
        } else {
            if (Flashback.isExporting()) {
                return false;
            } else {
                return !this.runsNormally() || entity == Minecraft.getInstance().player;
            }
        }
    }
}