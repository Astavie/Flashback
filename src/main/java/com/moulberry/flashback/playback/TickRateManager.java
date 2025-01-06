package com.moulberry.flashback.playback;

import com.moulberry.flashback.Flashback;
import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.Entity;

public class TickRateManager {
    public static final float MIN_TICKRATE = 1.0F;

    // Easiest way to sync between server and client: just make them static volatile
    private static volatile float tickrate = 20.0F;
    private static volatile long nanosecondsPerTick = TimeUtil.NANOSECONDS_PER_SECOND / 20L;
    private static volatile boolean isFrozen = false;

    private boolean runGameElements = true;
    private boolean isServerTickRateManager = false;

    public TickRateManager(boolean isServerTickRateManager) {
        this.isServerTickRateManager = isServerTickRateManager;
        if (this.isServerTickRateManager) {
            TickRateManager.setTickRate(20);
            TickRateManager.setFrozen(false);
        }
    }

    public static void setTickRate(float tickRate) {
        TickRateManager.tickrate = Math.max(tickRate, MIN_TICKRATE);
        TickRateManager.nanosecondsPerTick = (long)((double)TimeUtil.NANOSECONDS_PER_SECOND / (double)TickRateManager.tickrate);
    }

    public static void setFrozen(boolean frozen) {
        TickRateManager.isFrozen = frozen;
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