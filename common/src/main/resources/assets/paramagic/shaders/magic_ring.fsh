#version 330

in vec2 v_localXZ;

uniform float u_time;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;


float smoothRing(float r, float center, float width) {
    // 在 r 附近生成一条柔边环
    float inner = smoothstep(center - width, center, r);
    float outer = 1.0 - smoothstep(center, center + width, r);
    return clamp(inner * outer, 0.0, 1.0);
}

void main() {
    // 极坐标
    float r = length(v_localXZ);
    float a = atan(v_localXZ.y, v_localXZ.x); // (-pi..pi)

    // 一条旋转的虚线环
    float ring1 = smoothRing(r, 0.6, 0.01);
    float segs = 24.0;
    float dash = step(0.5, fract(a * segs / 6.2831853 + u_time * 0.5)); // 旋转虚线
    float layer1 = ring1 * dash;

    // 第二层缓慢流动的微光环
    float ring2 = smoothRing(r, 0.35 + 0.05 * sin(u_time * 0.7), 0.015);
    float layer2 = ring2 * (0.6 + 0.4 * sin(a * 5.0 + u_time * 1.2));

    // 中心脉动能量核
    float core = smoothstep(0.18, 0.0, r) * (0.5 + 0.5 * sin(u_time * 2.3));

    float intensity = layer1 + layer2 + core;

    // 低于阈值的像素直接丢弃，避免整块平面遮挡世界（无需开混合也能“镂空”）
    if (intensity < 0.02) discard;

    vec3 base = vec3(0.2, 0.8, 1.0); // 青色系
    vec3 color = base * (0.7 + 0.3 * sin(u_time * 3.0)) * intensity;

    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    if (brightness > 2.0) {
        o_bloomColor = vec4(color.rgb, 1.0);
    }

    o_color = vec4(color, 1.0);
}