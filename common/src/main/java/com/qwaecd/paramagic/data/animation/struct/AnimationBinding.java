package com.qwaecd.paramagic.data.animation.struct;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 存储单个绑定信息的类，注入动画操作发生时被消费的对象。
 */
public class AnimationBinding implements IDataSerializable {
    @Getter
    @Nonnull
    private final String targetNodeNameOrComponentId;
    /**
     * The name of an external animator template name, cannot coexist with an inline animator definition.<p>
     * 从外部获取的动画模板名称，不可与内联定义同时存在。
     */
    @Getter
    @Nullable
    private final String animatorTemplateName;
    @Getter
    @Nullable
    private final AnimatorData animatorData;
    public AnimationBinding(@Nonnull String targetNodeNameOrComponentId, @Nullable String animatorTemplateName, @Nullable AnimatorData animatorData) {
        if (animatorTemplateName != null && animatorData != null) {
            throw new IllegalArgumentException("Binding cannot have both 'animatorName' and an inline 'animator' definition.");
        }
        this.targetNodeNameOrComponentId = targetNodeNameOrComponentId;
        this.animatorTemplateName = animatorTemplateName;
        this.animatorData = animatorData;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString("target", this.targetNodeNameOrComponentId);
        codec.writeBoolean("hasTemplate", this.animatorTemplateName != null);
        if (this.animatorTemplateName != null) {
            codec.writeString("templateName", this.animatorTemplateName);
        } else {
            codec.writeObject("animatorData", this.animatorData);
        }
    }

    @Nullable
    public static AnimationBinding fromCodec(DataCodec codec) {
        final String target = codec.readString("target");
        final boolean hasTemplate = codec.readBoolean("hasTemplate");
        if (hasTemplate) {
            final String animatorTemplateName = codec.readString("templateName");
            return new AnimationBinding(target, animatorTemplateName, null);
        }
        final AnimatorData animatorData = AnimatorData.fromCodec(codec);
        if (animatorData != null) {
            return new AnimationBinding(target, null, animatorData);
        }
        return null;
    }
}
