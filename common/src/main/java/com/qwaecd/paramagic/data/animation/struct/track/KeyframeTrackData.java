package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;

import java.util.List;

public class KeyframeTrackData<T> extends TrackData<T> {
    public final List<KeyframeData<T>> keyframes;
    public final boolean loop;

    public KeyframeTrackData(AnimatableProperty<T> property, List<KeyframeData<T>> keyframes, boolean loop) {
        super(property);
        this.keyframes = keyframes;
        this.loop = loop;
    }

    public void addKeyframe(KeyframeData<T> keyframe) {
        this.keyframes.add(keyframe);
    }
}
