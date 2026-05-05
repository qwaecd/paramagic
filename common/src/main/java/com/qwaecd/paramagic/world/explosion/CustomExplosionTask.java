package com.qwaecd.paramagic.world.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;

import java.util.List;

final class CustomExplosionTask {
    private final CustomExplosionConfig config;
    private final List<SphereExplosionScanner.BlockOffset> offsets;
    private final Explosion vanillaContext;
    private int cursor = 0;
    private int destroyedBlocks = 0;
    private boolean finished = false;

    CustomExplosionTask(CustomExplosionConfig config, List<SphereExplosionScanner.BlockOffset> offsets, Explosion vanillaContext) {
        this.config = config;
        this.offsets = offsets;
        this.vanillaContext = vanillaContext;
    }

    ServerLevel getLevel() {
        return this.config.level;
    }

    void tick() {
        this.processBudget(this.config.blocksPerTick);
    }

    void processUntilFinished() {
        this.processBudget(Integer.MAX_VALUE);
    }

    boolean isFinished() {
        return this.finished;
    }

    private void processBudget(int budget) {
        if (this.finished) {
            return;
        }

        int processed = 0;
        while (this.cursor < this.offsets.size() && processed < budget) {
            if (this.destroyedBlocks >= this.config.maxDestroyedBlocks) {
                this.finish();
                return;
            }

            SphereExplosionScanner.BlockOffset offset = this.offsets.get(this.cursor++);
            processed++;
            if (CustomExplosion.processBlock(this.config, this.vanillaContext, offset)) {
                this.destroyedBlocks++;
            }
        }

        if (this.cursor >= this.offsets.size()) {
            this.finish();
        }
    }

    private void finish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        CustomExplosion.finish(this.config, this.destroyedBlocks);
    }
}
