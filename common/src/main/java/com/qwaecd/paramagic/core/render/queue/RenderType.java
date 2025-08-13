package com.qwaecd.paramagic.core.render.queue;

public enum RenderType {
    OPAQUE,   // 不透明
    CUTOUT,   // 遮罩/镂空（alpha test）
    TRANSPARENT, // 半透明（Alpha 混合）
    ADDITIVE  // 加色发光
}
