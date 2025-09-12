package com.qwaecd.paramagic.data.animation.track;

import java.util.List;

public class KeyframeTrackData<T> extends TrackData {
    public final List<KeyframeData<T>> keyframes;
    public final boolean loop;

    public KeyframeTrackData(String property, List<KeyframeData<T>> keyframes, boolean loop) {
        super(property);
        this.keyframes = keyframes;
        this.loop = loop;
    }
}
