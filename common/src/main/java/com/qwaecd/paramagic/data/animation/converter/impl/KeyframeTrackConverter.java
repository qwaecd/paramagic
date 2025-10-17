package com.qwaecd.paramagic.data.animation.converter.impl;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Keyframe;
import com.qwaecd.paramagic.client.animation.PropertyAccessor;
import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;
import com.qwaecd.paramagic.data.animation.converter.TrackConverter;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.para.converter.ConversionException;

import java.util.List;
import java.util.stream.Collectors;

public class KeyframeTrackConverter implements TrackConverter<KeyframeTrackData<?>> {
    @Override
    public AnimationTrack convert(KeyframeTrackData<?> trackData, Transform targetTransform, ParaMaterial material) throws ConversionException {
        PropertyAccessor<?> accessor = createAccessor(targetTransform, material, trackData.property);

        List<Keyframe<?>> runtimeKeyframes = trackData.keyframes.stream()
                .map(kd -> new Keyframe<>(kd.time(), kd.value(), kd.interpolation()))
                .collect(Collectors.toList());

        return new AnimationTrack(accessor, runtimeKeyframes, trackData.loop);
    }

    private <T> PropertyAccessor<T> createAccessor(Transform transform, ParaMaterial material, AnimatableProperty<T> animatableProperty) {
        return animatableProperty.getAccessorFactory().getAccessor(transform, material);
    }
}
