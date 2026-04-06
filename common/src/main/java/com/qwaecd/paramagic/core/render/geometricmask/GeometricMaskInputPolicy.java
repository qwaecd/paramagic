package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 几何遮罩效果读取场景输入的策略。当前管线中几何遮罩阶段位于 bloom 与 final compose 之后，
 * 默认应使用 {@link #POST_BLOOM_COMBINED}。
 */
public enum GeometricMaskInputPolicy {
    /** Paramagic HDR（未与原版合成），极少在遮罩阶段直接使用 */
    HDR_SCENE_PRE_COMPOSE,
    /** 与原版游戏颜色合成后的最终可见颜色纹理（推荐默认） */
    POST_BLOOM_COMBINED,
    /** 仅 bloom 模糊结果（预留） */
    BLOOM_ONLY,
    /** 原版场景拷贝（预留） */
    GAME_SCENE_COPY
}
