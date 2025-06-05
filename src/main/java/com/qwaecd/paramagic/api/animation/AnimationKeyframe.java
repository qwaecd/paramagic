package com.qwaecd.paramagic.api.animation;

import net.minecraft.util.Mth;
import org.joml.Vector2f;

import java.awt.*;

/**
 * Animation keyframe for property interpolation
 */
public class AnimationKeyframe {
    private Object startValue;
    private Object endValue;
    private EasingType easing;

    public AnimationKeyframe(Object startValue, Object endValue, EasingType easing) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.easing = easing;
    }

    public Object interpolate(float progress) {
        float t = easing.apply(progress);

        if (startValue instanceof Float && endValue instanceof Float) {
            return Mth.lerp(t, (Float)startValue, (Float)endValue);
        } else if (startValue instanceof Vector2f && endValue instanceof Vector2f) {
            Vector2f start = (Vector2f)startValue;
            Vector2f end = (Vector2f)endValue;
            return new Vector2f(
                    Mth.lerp(t, start.x, end.x),
                    Mth.lerp(t, start.y, end.y)
            );
        } else if (startValue instanceof Color && endValue instanceof Color) {
            Color start = (Color)startValue;
            Color end = (Color)endValue;
            return new Color(
                    (int)Mth.lerp(t, start.getRed(), end.getRed()),
                    (int)Mth.lerp(t, start.getGreen(), end.getGreen()),
                    (int)Mth.lerp(t, start.getBlue(), end.getBlue()),
                    (int)Mth.lerp(t, start.getAlpha(), end.getAlpha())
            );
        }

        return progress < 0.5f ? startValue : endValue;
    }
}