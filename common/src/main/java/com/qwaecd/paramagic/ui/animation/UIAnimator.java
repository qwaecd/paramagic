package com.qwaecd.paramagic.ui.animation;

import com.qwaecd.paramagic.tools.anim.Interpolator;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UIAnimator<T> {
    private final T start;
    private final T end;
    private final float duration;
    private float elapsedTime;
    private boolean finished;

    private boolean cycle = false;

    private final Interpolator<T> interpolator;
    private final ValueSetter<T> setter;

    @Nullable
    private Runnable onRemove;
    @Nullable
    private Consumer<T> onUpdate;

    public UIAnimator(
            T start,
            T end,
            float duration,
            Interpolator<T> interpolator,
            ValueSetter<T> setter
    ) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.interpolator = interpolator;
        this.setter = setter;
        this.elapsedTime = 0f;
        this.finished = false;
    }

    public void update(float deltaTime) {
        if (this.finished) {
            return;
        }

        this.elapsedTime += deltaTime;
        float alpha = Math.min(this.elapsedTime / this.duration, 1.0f);
        T currentValue = this.interpolator.interpolate(this.start, this.end, alpha);
        this.setter.set(currentValue, this.end);

        if (this.onUpdate != null) {
            this.onUpdate.accept(currentValue);
        }


        if (alpha >= 1.0f) {
            if (this.cycle) {
                this.elapsedTime = 0.0f;
                return;
            }
            this.finished = true;
            if (this.onRemove != null) {
                this.onRemove.run();
            }
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

    void kill() {
        this.finished = true;
        if (this.onRemove != null) {
            this.onRemove.run();
        }
    }

    public UIAnimator<T> setOnUpdate(Consumer<T> callback) {
        this.onUpdate = callback;
        return this;
    }
}
