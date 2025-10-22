package com.qwaecd.paramagic.data.animation.util;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;
import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeData;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.struct.track.TrackData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个链式调用式的时间线构建器, 是构建一个 AnimatorData 的辅助工具类.
 */
public final class TimelineBuilder {
    private float timePointer;
    private final Map<AnimatableProperty<?>, TrackData<?>> tracks;

    public TimelineBuilder() {
        this.timePointer = 0.0f;
        this.tracks = new HashMap<>();
    }

    public float currentTime() {
        return this.timePointer;
    }

    public TimelineBuilder at(float time) {
        if (time < 0.0f) {
            throw new IllegalArgumentException("Time cannot be negative.");
        }
        this.timePointer = time;
        return this;
    }

    public TimelineBuilder timeStep(float deltaTime) {
        this.timePointer += deltaTime;
        if (this.timePointer < 0.0f) {
            throw new IllegalArgumentException("Time cannot be negative.");
        }
        return this;
    }

    /**
     * Add a keyframe at the current time point, can only act on keyframe tracks.<br>
     * 向当前的时间点添加一个关键帧，只能作用于关键帧轨道.
     */
    public <T> TimelineBuilder keyFrame(AnimatableProperty<T> property, T value) {
        this.keyFrame(property, value, false);
        return this;
    }

    /**
     * Add a keyframe at the current time point, can only act on keyframe tracks.<br>
     * 向当前的时间点添加一个关键帧，只能作用于关键帧轨道.
     * @param loop 初次设定该轨道时指定轨道的循环属性，若该轨道已经创建了，则以第一次创建为准.
     */
    @SuppressWarnings("unchecked")
    public <T> TimelineBuilder keyFrame(AnimatableProperty<T> property, T value, boolean loop) {
        TrackData<?> trackData = this.tracks.get(property);
        if (trackData == null) {
            trackData = new KeyframeTrackData<>(
                    property,
                    new ArrayList<>(),
                    loop
            );
            this.tracks.put(property, trackData);
        }

        if (trackData instanceof KeyframeTrackData<?> kfT) {
            KeyframeTrackData<T> keyframeTrack = (KeyframeTrackData<T>) kfT;
            keyframeTrack.addKeyFrame(
                    new KeyframeData<>(this.timePointer, value)
            );
        } else {
            throw new IllegalArgumentException("Track for property " + property.getName() + " is not a KeyframeTrackData.");
        }

        return this;
    }

    public AnimatorData build() {
        List<TrackData<?>> trackList = new ArrayList<>();
        for (var entry : tracks.entrySet()) {
            TrackData<?> track = entry.getValue();
            trackList.add(track);
        }
        AnimatorData animatorData = new AnimatorData(trackList);

        this.reset();
        return animatorData;
    }

    private void reset() {
        this.tracks.clear();
        this.timePointer = 0.0f;
    }
}
