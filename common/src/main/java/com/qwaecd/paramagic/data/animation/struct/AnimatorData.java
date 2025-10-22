package com.qwaecd.paramagic.data.animation.struct;

import com.qwaecd.paramagic.data.animation.struct.track.TrackData;
import lombok.Getter;

import java.util.List;


/**
 * An animation data for a specific Para component, including multiple tracks.
 * <p>
 * 对需要被动画的 Para 组件的动画数据，包含多条轨道。
 */
public class AnimatorData {
    @Getter
    private final List<TrackData<?>> tracks;

    /**
     * Constructs an AnimatorData with the given tracks.<br>
     * If you are not very sure about what you are doing, please use TimelineBuilder to build AnimatorData.
     * <p>
     * 除非你非常清楚自己在做什么，否则请使用 TimelineBuilder 来构建 AnimatorData.
     * @see com.qwaecd.paramagic.data.animation.util.TimelineBuilder
     * @param tracks 一个包含所有轨道的列表.
     */
    public AnimatorData(List<TrackData<?>> tracks) {
        this.tracks = tracks;
    }
}
