package com.qwaecd.paramagic.client.renderer;

import com.qwaecd.paramagic.api.client.IClientRenderableMagicCircle;
import net.minecraft.core.BlockPos;

public abstract class BaseMagicCircleRenderer implements IClientRenderableMagicCircle {
    protected BlockPos center;
    protected Phase currentPhase;
    protected int tickCount;
    protected boolean finished;

    protected static final int BUILD_DURATION = 20;   // 1 second
    protected static final int SUSTAIN_DURATION = 60; // 3 seconds
    protected static final int DISSIPATE_DURATION = 40; // 2 seconds

    @Override
    public void startRender(BlockPos center, Object... parameters) {
        this.center = center;
        this.currentPhase = Phase.BUILD;
        this.tickCount = 0;
        this.finished = false;
    }

    @Override
    public void tick() {
        tickCount++;

        switch (currentPhase) {
            case BUILD:
                if (tickCount >= BUILD_DURATION) {
                    currentPhase = Phase.SUSTAIN;
                    tickCount = 0;
                }
                break;
            case SUSTAIN:
                if (tickCount >= SUSTAIN_DURATION) {
                    currentPhase = Phase.DISSIPATE;
                    tickCount = 0;
                }
                break;
            case DISSIPATE:
                if (tickCount >= DISSIPATE_DURATION) {
                    finished = true;
                }
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    protected float getPhaseProgress() {
        int maxDuration = switch (currentPhase) {
            case BUILD -> BUILD_DURATION;
            case SUSTAIN -> SUSTAIN_DURATION;
            case DISSIPATE -> DISSIPATE_DURATION;
        };
        return Math.min(1.0f, (float) tickCount / maxDuration);
    }
}
