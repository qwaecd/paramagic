package com.qwaecd.paramagic.data.animation;

import com.qwaecd.paramagic.data.animation.track.TrackData;
import lombok.Getter;

import java.util.List;


/**
 * An animation data for a specific Para component, including multiple tracks.
 * <p>
 * 对需要被动画的 Para 组件的动画数据，包含多条轨道。
 */
public class AnimationComponentData {
    @Getter
    private final String targetComponentId;
    @Getter
    private final List<TrackData> tracks;

    public AnimationComponentData(String targetComponentId, List<TrackData> tracks) {
        this.targetComponentId = targetComponentId;
        this.tracks = tracks;
    }
}
