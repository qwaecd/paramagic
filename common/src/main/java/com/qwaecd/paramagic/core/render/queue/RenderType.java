package com.qwaecd.paramagic.core.render.queue;


/**
 * 渲染类型枚举，用于区分不同的渲染方式。
 * <p>
 * OPAQUE: 不透明渲染，通常用于不需要透明度处理的物体。<p>
 * CUTOUT: 遮罩/镂空渲染，使用 alpha 测试来决定哪些像素被渲染。<p>
 * TRANSPARENT: 半透明渲染，使用 Alpha 混合来处理透明度。<p>
 * ADDITIVE: 加色发光渲染，通常用于发光效果。
 */
public enum RenderType {
    OPAQUE,   // 不透明
    CUTOUT,   // 遮罩/镂空（alpha test）
    TRANSPARENT, // 半透明（Alpha 混合）
    ADDITIVE  // 加色发光
}
