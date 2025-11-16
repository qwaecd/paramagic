package com.qwaecd.paramagic.data.animation.struct;

import com.qwaecd.paramagic.data.animation.struct.track.TrackData;
import com.qwaecd.paramagic.data.animation.struct.track.TrackTypeRegistry;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * An animation data for a specific Para component, including multiple tracks.
 * <p>
 * 对需要被动画的 Para 组件的动画数据，包含多条轨道。
 */
public class AnimatorData implements IDataSerializable {
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

    @Override
    public void write(DataCodec codec) {
        // 写入轨道数量
        codec.writeInt("trackCount", this.tracks.size());
        // 对每条轨道写入类型 ID 与具体内容
        for (int i = 0; i < this.tracks.size(); i++) {
            TrackData<?> track = this.tracks.get(i);
            @SuppressWarnings("unchecked")
            Class<? extends TrackData<?>> clazz = (Class<? extends TrackData<?>>) track.getClass();
            int typeId = TrackTypeRegistry.getTypeId(clazz);
            codec.writeInt("typeId_" + i, typeId);
            codec.writeObject("track_" + i, track);
        }
    }

    public static AnimatorData fromCodec(DataCodec codec) {
        int count = codec.readInt("trackCount");
        List<TrackData<?>> tracks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int typeId = codec.readInt("typeId_" + i);
            TrackData<?> track = TrackTypeRegistry.getFactory(typeId).apply(codec);
            tracks.add(track);
        }
        return new AnimatorData(tracks);
    }
}
