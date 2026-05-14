package com.qwaecd.paramagic.ui.animation;

import com.qwaecd.paramagic.tools.anim.Interpolator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UIAnimator<T> {
    @Nonnull
    private State state;
    private final T start;
    private final T end;
    private final float duration;
    private float elapsedTime;
    private boolean finished;

    private boolean cycle = false;

    private final Interpolator<T> interpolator;
    private final ValueSetter<T> setter;

    /**
     * 在动画器被最终从动画系统中移除时被调用
     */
    @Nullable
    private Runnable onRemove;

    /**
     * 当动画器自然完成时被调用
     */
    @Nullable
    private Runnable onComplete;

    /**
     * 当动画器因为外部调用而被取消时被调用
     */
    @Nullable
    private Runnable onCancel;

    @Nullable
    private Consumer<T> onUpdate;

    public UIAnimator(
            T start,
            T end,
            float duration,
            Interpolator<T> interpolator,
            ValueSetter<T> setter
    ) {
        this.state = State.RUNNING;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.interpolator = interpolator;
        this.setter = setter;
        this.elapsedTime = 0.0f;
        this.finished = false;
    }

    @Nonnull
    public State getState() {
        return this.state;
    }

    public void update(float deltaTime) {
        if (this.finished) {
            return;
        }

        this.elapsedTime += deltaTime;
        float alpha = Math.min(this.elapsedTime / this.duration, 1.0f);
        T currentValue = this.interpolator.interpolate(this.start, this.end, alpha);
        this.setter.set(currentValue);

        if (this.onUpdate != null) {
            this.onUpdate.accept(currentValue);
        }


        if (alpha >= 1.0f) {
            if (this.cycle) {
                this.elapsedTime = 0.0f;
                return;
            }
            this.complete();
        }
    }

    private void complete() {
        if (this.finished) {
            return;
        }
        this.state = State.COMPLETED;
        this.finished = true;
        if (this.onComplete != null) {
            this.onComplete.run();
        }
    }

    void cancel() {
        if (this.state != State.RUNNING) {
            return;
        }
        this.state = State.CANCELLED;
        this.finished = true;
        if (this.onCancel != null) {
            this.onCancel.run();
        }
    }

    void remove() {
        if (this.state == State.REMOVED) {
            return;
        }
        this.finished = true;
        this.state = State.REMOVED;
        if (this.onRemove != null) {
            this.onRemove.run();
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public UIAnimator<T> setCycle(boolean cycle) {
        this.cycle = cycle;
        return this;
    }

    public UIAnimator<T> setOnRemove(Runnable callback) {
        this.onRemove = callback;
        return this;
    }

    public UIAnimator<T> setOnCancel(Runnable callback) {
        this.onCancel = callback;
        return this;
    }

    public UIAnimator<T> setOnComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }

    public UIAnimator<T> setOnUpdate(Consumer<T> callback) {
        this.onUpdate = callback;
        return this;
    }

    public enum State {
        RUNNING,
        COMPLETED,
        CANCELLED,
        REMOVED
    }
}
