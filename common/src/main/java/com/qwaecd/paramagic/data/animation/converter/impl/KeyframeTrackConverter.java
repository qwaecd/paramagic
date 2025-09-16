package com.qwaecd.paramagic.data.animation.converter.impl;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Keyframe;
import com.qwaecd.paramagic.client.animation.PropertyAccessor;
import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.PropertyType;
import com.qwaecd.paramagic.data.animation.converter.TrackConverter;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.para.ConversionException;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.stream.Collectors;

public class KeyframeTrackConverter implements TrackConverter<KeyframeTrackData<?>> {
    @Override
    public AnimationTrack convert(KeyframeTrackData<?> trackData, Transform targetTransform, ParaMaterial material) throws ConversionException {
        PropertyAccessor<?> accessor = createAccessor(targetTransform, material, trackData.property.getName());

        List<Keyframe<?>> runtimeKeyframes = trackData.keyframes.stream()
                .map(kd -> new Keyframe<>(kd.time(), kd.value(), kd.interpolation()))
                .collect(Collectors.toList());

        return new AnimationTrack(accessor, runtimeKeyframes, trackData.loop);
    }

    private PropertyAccessor<?> createAccessor(Transform transform, ParaMaterial material, String propertyType) throws ConversionException {
        return switch (propertyType) {
            case "position" -> (PropertyAccessor<Vector3f>) transform::setPosition;
            case "rotation" -> (PropertyAccessor<Quaternionf>) transform::setRotation;
            case "scale" -> (PropertyAccessor<Vector3f>) transform::setScale;
            case "color" -> (PropertyAccessor<Vector4f>) material.animationColor::set;
            case "emissiveColor" -> (PropertyAccessor<Vector3f>) material::setEmissiveColor;
            case "emissiveIntensity" -> (PropertyAccessor<Float>) material::setEmissiveIntensity;
            default -> throw new ConversionException("Unknown property: " + propertyType);
        };
    }
}
