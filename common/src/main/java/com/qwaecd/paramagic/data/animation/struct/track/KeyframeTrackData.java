package com.qwaecd.paramagic.data.animation.struct.track;

import com.qwaecd.paramagic.data.animation.property.AnimatableProperty;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

import java.util.Arrays;
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
        codec.writeInt("count", this.keyframes.size());
        for (int i = 0; i < this.keyframes.size(); i++) {
            codec.writeObject("k_" + i, this.keyframes.get(i));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static KeyframeTrackData<?> fromCodec(DataCodec codec) {
        AnimatableProperty<?> property = codec.readObject("property", AnimatableProperty::fromCodec);
        boolean loop = codec.readBoolean("loop");
        int count = codec.readInt("count");

        KeyframeData<?>[] keyframes = new KeyframeData<?>[count];
        for (int i = 0; i < count; i++) {
            keyframes[i] = codec.readObject("k_" + i, KeyframeData::fromCodec);
        }

        // Convert to list and perform controlled cast to satisfy constructor's generic signature
        List<KeyframeData<?>> list = Arrays.asList(keyframes);
        return new KeyframeTrackData(property, list, loop);
    }
}
