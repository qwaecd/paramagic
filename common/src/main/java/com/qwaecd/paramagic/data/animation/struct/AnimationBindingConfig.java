package com.qwaecd.paramagic.data.animation.struct;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnimationBindingConfig implements IDataSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(AnimationBindingConfig.class);
    public static final String schemaVersion = "1.0";
    @Getter
    private final List<AnimationBinding> bindings;

    public AnimationBindingConfig(List<AnimationBinding> bindings) {
        this.bindings = bindings;
    }

    @Override
    public void write(DataCodec codec) {
        AnimationBinding[] bindings = this.bindings.toArray(new AnimationBinding[0]);
        codec.writeObjectArray("bindings", bindings);
    }

    @Nullable
    public static AnimationBindingConfig fromCodec(DataCodec codec) {
        try {
            IDataSerializable[] bindings = codec.readObjectArray("bindings", AnimationBinding::fromCodec);
            List<AnimationBinding> bindingList = new ArrayList<>();
            for (IDataSerializable binding : bindings) {
                if (binding == null) {
                    LOG.warn("Failed to decode AnimationBindingConfig from codec, null binding found.");
                    return null;
                }
                bindingList.add((AnimationBinding) binding);
            }
            return new AnimationBindingConfig(bindingList);
        } catch (Exception e) {
            LOG.warn("Failed to decode AnimationBindingConfig from codec", e);
            return null;
        }
    }
}
