package com.qwaecd.paramagic.ui.animation;

import com.qwaecd.paramagic.tools.anim.Interpolator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UIAnimator<V> extends BaseUIAnimator<UIAnimator<V>> {
    private V start;
    private V end;
    private V currentValue;

    private final Interpolator<V> interpolator;
    private final ValueSetter<V> setter;

    @Nullable
    private Consumer<V> onUpdate;

    public UIAnimator(
            V start,
            V end,
            float duration,
            Interpolator<V> interpolator,
            ValueSetter<V> setter
    ) {
        super(duration);
        this.start = start;
        this.end = end;
        this.currentValue = start;
        this.interpolator = interpolator;
        this.setter = setter;
    }

    @Override
    protected void apply(float alpha) {
        this.currentValue = this.interpolator.interpolate(this.start, this.end, alpha);
        this.setter.set(this.currentValue);

        if (this.onUpdate != null) {
            this.onUpdate.accept(this.currentValue);
        }
    }

    public UIAnimator<V> retarget(@Nonnull V newEnd, float newDuration) {
        if (this.state == UIAnimatorState.REMOVED) {
            return this;
        }
        this.start = this.currentValue;
        this.end = newEnd;
        this.duration = sanitizeDuration(newDuration);
        this.elapsedTime = 0.0f;
        this.finished = false;
        this.state = UIAnimatorState.RUNNING;
        return this;
    }

    public UIAnimator<V> retarget(@Nonnull V newEnd, @Nonnull RetargetDurationProvider durationProvider) {
        float oldDuration = this.duration;
        float elapsed = Math.min(this.elapsedTime, oldDuration);
        float remaining = Math.max(0.0f, oldDuration - elapsed);
        return this.retarget(newEnd, durationProvider.getDuration(elapsed, oldDuration, remaining));
    }

    public V getCurrentValue() {
        return this.currentValue;
    }

    public UIAnimator<V> setOnUpdate(Consumer<V> callback) {
        this.onUpdate = callback;
        return this;
    }
}
