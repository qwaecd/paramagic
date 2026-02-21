package com.qwaecd.paramagic.ui.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class UIAnimationSystem {
    private static UIAnimationSystem instance;
    private final List<UIAnimator<?>> animators = new ArrayList<>();

    private final Queue<UIAnimator<?>> pendingRemove = new ConcurrentLinkedQueue<>();

    public static UIAnimationSystem getInstance() {
        if (instance == null) {
            instance = new UIAnimationSystem();
        }
        return instance;
    }

    public void updateAll(float deltaTime) {
        while (this.pendingRemove.peek() != null) {
            UIAnimator<?> animator = this.pendingRemove.poll();
            this.animators.remove(animator);
        }
        var iterator = this.animators.iterator();
        while (iterator.hasNext()) {
            UIAnimator<?> animator = iterator.next();
            animator.update(deltaTime);
            if (animator.isFinished()) {
                animator.kill();
                iterator.remove();
            }
        }
    }

    public void addAnimator(UIAnimator<?> animator) {
        this.animators.add(animator);
    }

    public void removeAnimator(UIAnimator<?> animator) {
        this.pendingRemove.offer(animator);
    }
}
