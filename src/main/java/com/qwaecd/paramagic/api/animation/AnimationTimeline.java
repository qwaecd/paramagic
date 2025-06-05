package com.qwaecd.paramagic.api.animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationTimeline {
    private float duration;
    private float currentTime;
    private boolean loop;
    private float delay;
    private EasingType easing;
    private boolean active;
    private Map<String, AnimationKeyframe> keyframes;

    public AnimationTimeline(float duration, boolean loop, float delay, EasingType easing) {
        this.duration = duration;
        this.loop = loop;
        this.delay = delay;
        this.easing = easing;
        this.currentTime = 0;
        this.active = true;
        this.keyframes = new HashMap<>();
    }

    public void update(float deltaTime) {
        if (!active) return;

        currentTime += deltaTime;

        if (currentTime < delay) return;

        float animTime = currentTime - delay;
        if (animTime >= duration) {
            if (loop) {
                currentTime = delay + (animTime % duration);
            } else {
                currentTime = delay + duration;
                active = false;
            }
        }
    }

    public float getProgress() {
        if (currentTime < delay) return 0;
        float animTime = Math.min(currentTime - delay, duration);
        return easing.apply(animTime / duration);
    }

    public boolean isActive() {
        return active;
    }

    public void reset() {
        currentTime = 0;
        active = true;
    }

    public void addKeyframe(String property, AnimationKeyframe keyframe) {
        keyframes.put(property, keyframe);
    }

    public AnimationKeyframe getKeyframe(String property) {
        return keyframes.get(property);
    }
}
