#version 330

in vec3 v_viewPos;
in vec3 v_viewNormal;

uniform float u_time;

out vec4 o_color;

void main() {
    vec3 N = normalize(v_viewNormal);
    vec3 V = normalize(-v_viewPos);

    // 边缘更亮的“外发光”强度
    float rim = pow(1.0 - max(dot(N, V), 0.0), 2.5);

    // 让外壳有一点“呼吸”闪烁（可选，幅度很小）
    float flicker = 0.85 + 0.15 * sin(u_time * 2.0);

    // 外壳颜色与透明度（可按喜好调整）
    vec3 glowColor = vec3(0.3, 0.8, 1.0);
    float alpha = clamp(rim * 0.6, 0.0, 1.0) * flicker;

    // 强度太低的像素直接丢弃，减少无意义覆盖
    if (alpha < 0.02) discard;

    o_color = vec4(glowColor * (alpha), alpha); // 建议配合“加色”混合
}