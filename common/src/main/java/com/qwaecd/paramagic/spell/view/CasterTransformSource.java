package com.qwaecd.paramagic.spell.view;

import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public interface CasterTransformSource {
    /**
     * 将当前帧下的变换应用到 dist 上
     */
    void applyTo(TransformSample dist);
}
