package com.qwaecd.paramagic.data.animation.property;


import com.qwaecd.paramagic.client.animation.AccessorFactory;
import com.qwaecd.paramagic.network.codec.CodecException;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import javax.annotation.Nullable;

public class AnimatableProperty<T> implements IDataSerializable {
    @Getter
    private final String name;
    @Getter
    private final Class<T> valueClass;
    @Getter
    private final AccessorFactory<T> accessorFactory;

    AnimatableProperty(String name, Class<T> valueClass, AccessorFactory<T> accessorFactory) {
        this.name = name;
        this.valueClass = valueClass;
        this.accessorFactory = accessorFactory;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("name", this.name);
    }

    @Nullable
    public static AnimatableProperty<?> fromCodec(DataCodec codec) {
        String s = codec.readString("name");
        return AllAnimatableProperties.forName(s);
    }
}
