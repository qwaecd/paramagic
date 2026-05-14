package com.qwaecd.paramagic.ui.animation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseUIAnimator<T extends BaseUIAnimator<T>> {
    protected static final float MIN_DURATION = 0.0001f;
    @Nonnull
    protected UIAnimatorState state;
    protected float duration;
    protected float elapsedTime;
    protected boolean finished;

    protected boolean cycle = false;

    /**
     * 在动画器被最终从动画系统中移除时被调用
     */
    @Nullable
    protected Runnable onRemove;

    /**
     * 当动画器自然完成时被调用
     */
    @Nullable
    protected Runnable onComplete;

    /**
     * 当动画器因为外部调用而被取消时被调用
     */
    @Nullable
    protected Runnable onCancel;

    protected BaseUIAnimator(float duration) {
        this.state = UIAnimatorState.RUNNING;
        this.duration = sanitizeDuration(duration);
        this.elapsedTime = 0.0f;
        this.finished = false;
    }

    @Nonnull
    public UIAnimatorState getState() {
        return this.state;
    }

    public void update(float deltaTime) {
        if (this.finished) {
            return;
        }

        this.elapsedTime += deltaTime;
        float alpha = Math.min(this.elapsedTime / this.duration, 1.0f);
        this.apply(alpha);

        if (alpha >= 1.0f) {
            if (this.cycle) {
                this.elapsedTime = 0.0f;
                return;
            }
            this.complete();
        }
    }

    protected abstract void apply(float alpha);

    void complete() {
        if (this.finished) {
            return;
        }
        this.state = UIAnimatorState.COMPLETED;
        this.finished = true;
        if (this.onComplete != null) {
            this.onComplete.run();
        }
    }

    void cancel() {
        if (this.state != UIAnimatorState.RUNNING) {
            return;
        }
        this.state = UIAnimatorState.CANCELLED;
        this.finished = true;
        if (this.onCancel != null) {
            this.onCancel.run();
        }
    }

    void remove() {
        if (this.state == UIAnimatorState.REMOVED) {
            return;
        }
        this.finished = true;
        this.state = UIAnimatorState.REMOVED;
        if (this.onRemove != null) {
            this.onRemove.run();
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public BaseUIAnimator<T> setCycle(boolean cycle) {
        this.cycle = cycle;
        return this;
    }

    public BaseUIAnimator<T> setOnRemove(Runnable callback) {
        this.onRemove = callback;
        return this;
    }

    public BaseUIAnimator<T> setOnCancel(Runnable callback) {
        this.onCancel = callback;
        return this;
    }

    public BaseUIAnimator<T> setOnComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }

    @FunctionalInterface
    public interface RetargetDurationProvider {
        float getDuration(float elapsedTime, float oldDuration, float remainingTime);
    }

    protected static float sanitizeDuration(float duration) {
        return Math.max(MIN_DURATION, duration);
    }
}
