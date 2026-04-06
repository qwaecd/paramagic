package com.qwaecd.paramagic.core.render.geometricmask;

/**
 * 效果 pass 输出写回场景时的混合策略。当前全屏 resolve 多为整幅覆盖。
 */
public enum GeometricMaskBlendPolicy {
    OVERWRITE,
    ALPHA_BLEND,
    ADDITIVE
}
