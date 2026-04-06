package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 几何遮罩 effect pass 可选的场景输入纹理集合，由 {@link com.qwaecd.paramagic.core.render.ModRenderSystem} 在每帧填充。
 */
public record GeometricMaskSceneTextures(
        int postBloomCombinedTextureId,
        int hdrModCompositeTextureId,
        int bloomOnlyTextureId,
        int gameSceneCopyTextureId
) {
    /**
     * 按策略选取本道 effect 采样的 场景 纹理。
     * <p>
     * 多 caster 链式处理时，仅第一道使用此处解析结果；后续道使用上一道输出。
     */
    public int resolve(GeometricMaskInputPolicy policy) {
        return switch (policy) {
            case HDR_SCENE_PRE_COMPOSE -> hdrModCompositeTextureId;
            case POST_BLOOM_COMBINED -> postBloomCombinedTextureId;
            case BLOOM_ONLY -> bloomOnlyTextureId;
            case GAME_SCENE_COPY -> gameSceneCopyTextureId;
        };
    }
}
