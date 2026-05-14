package com.qwaecd.paramagic.ui.animation.fast;

import com.qwaecd.paramagic.ui.animation.BaseUIAnimator;
import com.qwaecd.paramagic.ui.animation.UIAnimatorState;

import javax.annotation.Nullable;

public class FloatUIAnimator extends BaseUIAnimator<FloatUIAnimator> {
    private float start;
    private float end;
    private float currentValue;

    private final FloatInterpolator interpolator;
    private final FloatValueSetter setter;

    @Nullable
    private FloatUpdateConsumer onUpdate;

    public interface FloatValueSetter {
        void set(float interpolationValue);
    }

    public interface FloatInterpolator {
        float interpolate(float start, float end, float alpha);
    }

    public interface FloatUpdateConsumer {
        void accept(float currentValue);
    }

    public FloatUIAnimator(
            float start,
            float end,
            float duration,
            FloatInterpolator interpolator,
            FloatValueSetter setter
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

    public float getCurrentValue() {
        return this.currentValue;
    }

    public FloatUIAnimator retarget(float newEnd, float newDuration) {
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

    public FloatUIAnimator retarget(float newEnd, RetargetDurationProvider durationProvider) {
        float oldDuration = this.duration;
        float elapsed = Math.min(this.elapsedTime, oldDuration);
        float remaining = Math.max(0.0f, oldDuration - elapsed);
        return this.retarget(newEnd, durationProvider.getDuration(elapsed, oldDuration, remaining));
    }

    public FloatUIAnimator setOnUpdate(FloatUpdateConsumer callback) {
        this.onUpdate = callback;
        return this;
    }
}
