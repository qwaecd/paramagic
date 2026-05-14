package com.qwaecd.paramagic.ui.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class UIAnimationSystem {
    private final List<UIAnimator<?>> animators = new ArrayList<>();

    private final Queue<UIAnimator<?>> pendingRemove = new ConcurrentLinkedQueue<>();

    public UIAnimationSystem() {
    }

    public void updateAll(float deltaTime) {
        while (this.pendingRemove.peek() != null) {
            UIAnimator<?> animator = this.pendingRemove.poll();
            this.animators.remove(animator);
            animator.remove();
        }
        var iterator = this.animators.iterator();
        while (iterator.hasNext()) {
            UIAnimator<?> animator = iterator.next();
            animator.update(deltaTime);
            if (animator.isFinished()) {
                animator.remove();
                iterator.remove();
            }
        }
    }

    public void addAnimator(UIAnimator<?> animator) {
        this.animators.add(animator);
    }

    public void removeAnimator(UIAnimator<?> animator) {
        this.pendingRemove.offer(animator);
        animator.cancel();
    }

    public void close() {
        for (var animator : this.animators) {
            if (!animator.isFinished()) {
                animator.cancel();
            }
            animator.remove();
        }
        for (var animator : this.pendingRemove) {
            if (!animator.isFinished()) {
                animator.cancel();
            }
            animator.remove();
        }
        this.animators.clear();
        this.pendingRemove.clear();
    }
}
