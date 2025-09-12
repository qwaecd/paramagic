package com.qwaecd.paramagic.data.animation.converter.impl;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Keyframe;
import com.qwaecd.paramagic.client.animation.PropertyAccessor;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.converter.TrackConverter;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.para.ConversionException;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;

public class KeyframeTrackConverter implements TrackConverter<KeyframeTrackData<?>> {
    @Override
    public AnimationTrack convert(KeyframeTrackData<?> trackData, Transform targetTransform) throws ConversionException {
        PropertyAccessor<?> accessor = createAccessor(targetTransform, trackData.property);

        List<Keyframe<?>> runtimeKeyframes = trackData.keyframes.stream()
                .map(kd -> new Keyframe<>(kd.time(), kd.value(), kd.interpolation()))
                .collect(Collectors.toList());

        return new AnimationTrack(accessor, runtimeKeyframes, trackData.loop);
    }

    private PropertyAccessor<?> createAccessor(Transform transform, String propertyName) throws ConversionException {
        return switch (propertyName) {
            case "position" -> (PropertyAccessor<Vector3f>) transform::setPosition;
            case "rotation" -> (PropertyAccessor<Quaternionf>) transform::setRotation;
            case "scale" -> (PropertyAccessor<Vector3f>) transform::setScale;
            default -> throw new ConversionException("Unknown property: " + propertyName);
        };
    }
}
