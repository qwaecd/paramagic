package com.qwaecd.paramagic.core.particle.effect;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EffectManager {
    private final IDManager idManager;
    private final int maxEffectCount;
    private final GPUParticleEffect[] activeEffects;

    private final AtomicInteger currentEffectCount = new AtomicInteger(0);

    public EffectManager(int maxEffectCount) {
        this.maxEffectCount = maxEffectCount;
        this.idManager = new IDManager();
        this.activeEffects = new GPUParticleEffect[maxEffectCount];
    }

    public int getCurrentEffectCount() {
        return this.currentEffectCount.get();
    }

    public void forEachActiveEffect(Consumer<GPUParticleEffect> consumer) {
        for (GPUParticleEffect effect : this.activeEffects) {
            if (effect != null) {
                consumer.accept(effect);
            }
        }
    }

    public boolean spawnEffect(GPUParticleEffect effect) {
        int id = this.idManager.acquireId();
        if (id == -1) {
            return false;
        }
        this.activeEffects[id] = effect;
        effect.setEffectId(id);
        this.currentEffectCount.incrementAndGet();
        return true;
    }

    public void removeEffect(GPUParticleEffect effect) {
        if (effect == null) {
            return;
        }
        int id = effect.getEffectId();
        if (id >= 0 && id < this.maxEffectCount && this.activeEffects[id] == effect) {
            this.activeEffects[id] = null;
            this.idManager.releaseId(id);
            this.currentEffectCount.decrementAndGet();
        }
    }

    private class IDManager {
        private final Deque<Integer> availableIds;

        private IDManager() {
            this.availableIds = new ConcurrentLinkedDeque<>();
            // 将所有ID倒序入栈 (63, 62, ..., 0)
            for (int i = maxEffectCount - 1; i >= 0; i--) {
                this.availableIds.push(i);
            }
        }

        /**
         * 获取一个可用的 Effect ID。
         * @return 可用的ID，如果没有可用的ID则返回 -1。
         */
        private int acquireId() {
            Integer id = this.availableIds.poll();
            return (id != null) ? id : -1;
        }

        /**
         * 释放一个 Effect ID，使其可被重新使用。
         * @param id 要释放的ID。
         */
        private void releaseId(int id) {
            if (id >= 0 && id < maxEffectCount) {
                this.availableIds.add(id);
            }
        }
    }
}
