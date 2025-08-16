#version 330 core
in vec2 v_localXZ;

layout(location = 0) out vec4 FragColor;
layout(location = 1) out vec4 BloomColor;

uniform float u_time;
uniform vec4  u_baseColor;

// 环参数（给你最多 8 条环线，够测试用）
#define MAX_RINGS 8
uniform int   u_ringCount;
uniform float u_ringR[MAX_RINGS];     // 半径
uniform float u_ringW[MAX_RINGS];     // 线宽（世界空间）
uniform float u_ringI[MAX_RINGS];     // 强度系数

// 刻度与径向条纹
uniform int   u_sectors;      // 扇区数（刻度）
uniform float u_tickWidth;    // 刻度线宽（角度方向）
uniform float u_bandCount;    // 径向条纹数量（每单位半径条数）
uniform float u_bandWidth;    // 条纹宽度

// 扫掠扇形
uniform float u_sweepSpeed;   // 扫描速度（圈/秒）
uniform float u_sweepWidth;   // 扇形宽度（角度归一化宽度）

// 中心核心
uniform float u_coreInner;    // 核心半径

// 抗锯齿工具
float aastep(float t, float x){
    float w = fwidth(x) * 0.5;
    return smoothstep(t - w, t + w, x);
}

float ring(vec2 p, float r0, float w){
    float d = abs(length(p) - r0);
    return 1.0 - aastep(w, d);
}

float sectorTicks(float theta, int sectors, float tickW){
    if (sectors <= 0) return 0.0;
    float t = fract(theta / (2.0*3.14159265) * float(sectors));
    t = min(t, 1.0 - t);
    return 1.0 - aastep(tickW, t);
}

float radialBands(vec2 p, float count, float bandW){
    if (count <= 0.0) return 0.0;
    float u = fract(length(p) * count);
    u = min(u, 1.0 - u);
    return 1.0 - aastep(bandW, u);
}

void main(){
    vec2 p = v_localXZ;
    float r = length(p);
    // 可选裁剪（你的平面网格若比 1 大，可以去掉这行）
    if (r > 1.25) discard;

    float theta = atan(p.y, p.x);

    // 1) 多条环线
    float I = 0.0;
    for (int i=0; i<MAX_RINGS; ++i){
        if (i >= u_ringCount) break;
        I += ring(p, u_ringR[i], u_ringW[i]) * u_ringI[i];
    }

    // 2) 刻度 + 仅在指定环带上出现（提升层次感）
    float bandMask = smoothstep(0.60, 0.62, r) * smoothstep(0.98, 0.96, r);
    I += sectorTicks(theta, u_sectors, u_tickWidth) * 0.7 * bandMask;

    // 3) 径向条纹（r 方向等距）
    I += radialBands(p, u_bandCount, u_bandWidth) * 0.6 * bandMask;

    // 4) 扫掠扇形（绕圈）
    float sweep = fract(theta/(2.0*3.14159265) + u_time * u_sweepSpeed);
    I += (1.0 - aastep(u_sweepWidth, abs(sweep - 0.5))) * 0.7
    * smoothstep(0.70, 0.80, r) * smoothstep(0.95, 0.88, r);

    // 5) 中心核心
    I += smoothstep(u_coreInner, 0.0, r) * 0.5;

    // 颜色与输出（建议走 ADDITIVE 桶）
    vec3 col = u_baseColor.rgb;

    float brightness = dot((col * I).rgb, vec3(0.2126, 0.7152, 0.0722));

    // Bloom 通道：默认不影响（alpha=0），仅在亮度很高时写入
    BloomColor = vec4(0.0, 0.0, 0.0, 0.0);
    if (brightness > 2.0) {
        BloomColor = vec4((col * I).rgb, 1.0);
    }

    // 加色混合：直接输出调制后的颜色
    FragColor = vec4(col * I, 1.0);
}