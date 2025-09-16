package com.qwaecd.paramagic.data.animation.track;

import com.qwaecd.paramagic.data.animation.PropertyType;

import java.util.List;

public class KeyframeTrackData<T> extends TrackData<T> {
    public final List<KeyframeData<T>> keyframes;
    public final boolean loop;

    public KeyframeTrackData(PropertyType<T> property, List<KeyframeData<T>> keyframes, boolean loop) {
        super(property);
        this.keyframes = keyframes;
        this.loop = loop;
    }
}
