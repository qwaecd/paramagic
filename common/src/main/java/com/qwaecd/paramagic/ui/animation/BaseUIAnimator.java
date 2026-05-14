package com.qwaecd.paramagic.ui.animation;

import com.qwaecd.paramagic.tools.anim.EasingFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseUIAnimator<Self extends BaseUIAnimator<Self>> {
    protected static final float MIN_DURATION = 0.0001f;
    @Nonnull
    protected UIAnimatorState state;
    protected float duration;
    protected float elapsedTime;
    protected boolean finished;

    protected boolean cycle = false;

    @Nonnull
    protected final EasingFunction easingFunction;

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

    protected BaseUIAnimator(float duration, @Nonnull EasingFunction easingFunction) {
        this.state = UIAnimatorState.RUNNING;
        this.duration = sanitizeDuration(duration);
        this.elapsedTime = 0.0f;
        this.finished = false;
        this.easingFunction = easingFunction;
    }

    protected BaseUIAnimator(float duration) {
        this(duration, EasingFunction.linear);
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

        final float raw;
        if (this.duration <= 0.0f) {
            raw = 1.0f;
        } else {
            raw = this.elapsedTime / this.duration;
        }
        final float alpha = this.easingFunction.ease(raw);
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

    @SuppressWarnings("unchecked")
    private Self self() {
        return (Self) this;
    }

    public Self setCycle(boolean cycle) {
        this.cycle = cycle;
        return this.self();
    }

    public Self setOnRemove(Runnable callback) {
        this.onRemove = callback;
        return this.self();
    }

    public Self setOnCancel(Runnable callback) {
        this.onCancel = callback;
        return this.self();
    }

    public Self setOnComplete(Runnable callback) {
        this.onComplete = callback;
        return this.self();
    }

    @FunctionalInterface
    public interface RetargetDurationProvider {
        float getDuration(float elapsedTime, float oldDuration, float remainingTime);
    }

    protected static float sanitizeDuration(float duration) {
        return Math.max(MIN_DURATION, duration);
    }
}
