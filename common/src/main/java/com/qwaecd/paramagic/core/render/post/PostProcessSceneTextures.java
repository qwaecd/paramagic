package com.qwaecd.paramagic.core.render.post;

/**
 * {@link PostProcessingManager} 一帧内可供几何遮罩等阶段引用的纹理。
 *
 * @param hdrModCompositeTextureId Paramagic 侧 HDR 场景与 bloom 合成后的结果（未与原版游戏颜色混合）
 * @param blurredBloomTextureId      Bloom 模糊链输出的辉光纹理（与 composite 中使用的同源）
 */
public record PostProcessSceneTextures(int hdrModCompositeTextureId, int blurredBloomTextureId) {
}
