package com.moulberry.flashback.playback;

import net.minecraft.client.Timer;

public class ReplayTimer extends Timer {

    public final TickRateManager manager;

    public ReplayTimer(long l, TickRateManager manager) {
        super(manager.tickrate(), l);
        this.manager = manager;
    }

    @Override
    public int advanceTime(long l) {
        this.msPerTick = manager.millisecondsPerTick();
        return super.advanceTime(l);
    }
}
