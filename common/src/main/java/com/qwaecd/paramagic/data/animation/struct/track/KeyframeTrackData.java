package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

import java.util.ArrayList;
import java.util.List;

public class KeyframeTrackData<T> extends TrackData<T> implements IDataSerializable {
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

    @Override
    public void write(DataCodec codec) {
        codec.writeObject("property", this.property);
        codec.writeBoolean("loop", this.loop);
        codec.writeObjectArray("keyframes", this.keyframes.toArray(new KeyframeData[0]));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static KeyframeTrackData<?> fromCodec(DataCodec codec) {
        AnimatableProperty<?> nullableProperty = codec.readObject("property", AnimatableProperty::fromCodec);
        if (nullableProperty == null) {
            throw new NullPointerException("AnimatableProperty read from codec is null");
        }

        boolean loop = codec.readBoolean("loop");

        IDataSerializable[] keyframeArray = codec.readObjectArray("keyframes", KeyframeData::fromCodec);

        List<KeyframeData<?>> list = new ArrayList<>();
        for (IDataSerializable data : keyframeArray) {
            list.add((KeyframeData<?>) data);
        }

        return new KeyframeTrackData(nullableProperty, list, loop);
    }
}
