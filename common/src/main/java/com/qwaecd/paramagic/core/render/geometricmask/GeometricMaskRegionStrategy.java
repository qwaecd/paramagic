package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 几何遮罩区域如何写入 GPU。主路径使用 {@link #MASK_TEXTURE}：mesh 片元写入 mask FBO，
 * 支持软边与多通道。模板缓冲仅作可选优化（硬边裁剪、减少 overdraw），不作为主要数据载体。
 */
public enum GeometricMaskRegionStrategy {
    MASK_TEXTURE,
    /** 可选：与 mask 纹理配合，不作为主要 mask 数据源 */
    STENCIL_OPTIONAL
}
