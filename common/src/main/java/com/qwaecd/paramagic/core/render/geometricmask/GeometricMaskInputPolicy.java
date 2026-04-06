package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 几何遮罩 effect pass 采样哪张「场景」纹理。由 {@link GeometricMaskSceneTextures#resolve(GeometricMaskInputPolicy)} 解析为具体 GL 纹理 id。
 */
public enum GeometricMaskInputPolicy {
    /**
     * Paramagic 侧：经 {@link com.qwaecd.paramagic.core.render.post.PostProcessingManager} 合成后的 HDR（含 bloom），
     * 尚未与原版 {@link com.qwaecd.paramagic.core.render.post.buffer.SceneCopyFBO} 混合。
     */
    HDR_SCENE_PRE_COMPOSE,
    /**
     * 最终可见颜色：Paramagic HDR 合成结果与原版游戏颜色在 {@code combinedSceneFbo} 中混合后的纹理（推荐默认）。
     */
    POST_BLOOM_COMBINED,
    /** 仅 Bloom 模糊输出（高光能量经模糊后的纹理），不含 base HDR 场景 */
    BLOOM_ONLY,
    /** 原版 Minecraft 主目标颜色拷贝（与 Paramagic 层无关） */
    GAME_SCENE_COPY
}
