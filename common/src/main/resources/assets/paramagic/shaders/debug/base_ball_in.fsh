#version 330

in vec3 v_viewPos;
in vec3 v_viewNormal;

uniform float u_time; // 可选，用于微动画

out vec4 o_color;

void main() {
    // 视图空间下的单位向量
    vec3 N = normalize(v_viewNormal);
    vec3 V = normalize(-v_viewPos); // 指向相机

    // 简单“面向光”的明暗（固定一个光方向即可）
    vec3 L = normalize(vec3(0.3, 0.7, 0.6));
    float lambert = max(dot(N, L), 0.0);

    // 边缘高亮（Fresnel 的直观近似）：越接近轮廓越亮
    float rim = pow(1.0 - max(dot(N, V), 0.0), 3.0);

    // 颜色：主体色 + 稍微一点边缘提亮
    vec3 baseColor = vec3(0.2, 0.6, 1.0);
    vec3 rimColor  = vec3(0.6, 0.9, 1.0);

    vec3 color = baseColor * (0.2 + 0.8 * lambert) + rimColor * (0.25 * rim);

    o_color = vec4(color, 1.0); // 本体不透明
}