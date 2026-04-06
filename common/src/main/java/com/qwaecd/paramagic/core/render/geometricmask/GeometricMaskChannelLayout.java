package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 约定几何遮罩 FBO 颜色附件的语义，便于多效果复用同一套资源。
 * <p>
 * 扭曲类效果当前使用：<br>
 * RG — UV 偏移<br>
 * B — 可选强度/调试用<br>
 * A — 未使用或留给权重
 */
public final class GeometricMaskChannelLayout {
    private GeometricMaskChannelLayout() {
    }

    /** 单通道权重遮罩（通用局部后处理） */
    public static final String DOC_WEIGHT_IN_R = "R=maskWeight, GBA=unused_or_reserved";
    /** 扭曲场 */
    public static final String DOC_DISTORTION_FIELD = "RG=uvOffset, B=optionalMagnitude, A=unused";
}
