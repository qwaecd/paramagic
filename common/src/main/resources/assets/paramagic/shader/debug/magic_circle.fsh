#version 330 core
in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uMaskTex;      // 你的 128x128 法阵 png（白+透明）
uniform vec4 uColorInner;        // 内部颜色
uniform vec4 uColorOuter;        // 外部颜色（渐变用）
uniform float uTime;             // 秒
uniform float uRotationSpeed;    // 弧度/秒（仅用于UV旋转）
uniform float uPulseSpeed;       // Hz
uniform float uPulseAmp;         // 0..1
uniform float uSweepSpeed;       // 周/秒
uniform float uSweepWidth;       // 0..1
uniform float uIntensity;        // 发光倍增
uniform vec2  uUVScale;          // UV 缩放
uniform vec2  uUVOffset;         // UV 偏移
uniform vec2  uTexelSize;        // 1.0 / textureSize(uMaskTex, 0)
uniform float uEdgeAA;           // 抗锯齿

const float PI = 3.14159265359;
const float TAU = 6.28318530718;

vec2 rotateUV(vec2 uv, float angle) {
    float s = sin(angle), c = cos(angle);
    mat2 R = mat2(c,-s, s, c);
    return (R * (uv - 0.5)) + 0.5;
}

vec2 toPolar(vec2 uv) {
    vec2 p = uv - 0.5;
    float r = length(p) * 2.0;
    float a = atan(p.y, p.x); // [-PI, PI]
    return vec2(r, a);
}

void main() {
    float angle = uTime * uRotationSpeed;
    vec2 uv = rotateUV(vUV * uUVScale + uUVOffset, angle);

    float rawA = texture(uMaskTex, uv).a;

    // 简易抗锯齿：用 fwidth 做边缘软化
    float w = max(fwidth(rawA), uEdgeAA);
    float mask = smoothstep(0.5 - w, 0.5 + w, rawA);

    // 极坐标扫光
    vec2 pol = toPolar(uv);
    float r01 = clamp(pol.x, 0.0, 1.0);
    float ang01 = fract((pol.y / TAU) + 0.5);
    float sweepPos = fract(ang01 - uTime * uSweepSpeed);
    float sweep = 1.0 - smoothstep(0.0, uSweepWidth, sweepPos);
    sweep *= mask;

    // 脉冲
    float pulse = 1.0 + uPulseAmp * sin(TAU * uPulseSpeed * uTime);

    // 渐变颜色
    vec3 gradColor = mix(uColorInner.rgb, uColorOuter.rgb, r01);

    vec3 emission = (gradColor * mask + gradColor * sweep * 0.7) * uIntensity * pulse;

    // 对加色混合，给个合理 alpha 便于切换其它混合
    float outAlpha = mask;
    FragColor = vec4(emission, outAlpha);
}