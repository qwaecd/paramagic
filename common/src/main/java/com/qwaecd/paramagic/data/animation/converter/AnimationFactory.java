package com.qwaecd.paramagic.data.animation.converter;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Animator;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.AnimationComponentData;
import com.qwaecd.paramagic.data.animation.AnimationData;
import com.qwaecd.paramagic.data.animation.converter.impl.KeyframeTrackConverter;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.track.TrackData;
import com.qwaecd.paramagic.data.para.ConversionException;
import com.qwaecd.paramagic.feature.MagicCircle;

import java.util.HashMap;
import java.util.Map;

public class AnimationFactory {
    private final Map<Class<? extends TrackData>, TrackConverter<?>> converterRegistry;

    public AnimationFactory() {
        this.converterRegistry = new HashMap<>();
        registerDefaultConverters();
    }

    private void registerDefaultConverters() {
        register(KeyframeTrackData.class, new KeyframeTrackConverter());
    }
    private void register(Class<? extends TrackData> dataClass, TrackConverter<?> converter) {
        converterRegistry.put(dataClass, converter);
    }

    public void injectAnimations(MagicCircle circle, AnimationData animations) throws ConversionException {
        validateInputs(circle, animations);
        for (AnimationComponentData compAnim : animations.getAnimationComponentDataList()) {
            for (TrackData track : compAnim.getTracks()) {
                attachAnimatorTrack(track, circle.getTransform(), circle.getOrCreateAnimator());
            }
        }
    }
    private void validateInputs(MagicCircle circle, AnimationData animations) throws ConversionException {
        if (circle == null) {
            throw new ConversionException("MagicCircle cannot be null");
        }
        if (animations == null) {
            throw new ConversionException("AnimationData cannot be null");
        }
    }

    private void attachAnimatorTrack(TrackData trackData, Transform transform, Animator animator) throws ConversionException {
        convertAndAddTrack(trackData, transform, animator);
    }

    @SuppressWarnings("unchecked")
    private <T extends TrackData> void convertAndAddTrack(T trackData, Transform targetTransform, Animator animator)
            throws ConversionException
    {
        TrackConverter<T> converter = (TrackConverter<T>) converterRegistry.get(trackData.getClass());

        if (converter != null) {
            AnimationTrack runtimeTrack = converter.convert(trackData, targetTransform);
            animator.addTrack(runtimeTrack);
        } else {
            throw new ConversionException("No converter found for " + trackData.getClass().getSimpleName());
        }
    }
}
