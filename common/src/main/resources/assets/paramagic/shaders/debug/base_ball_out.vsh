#version 330

layout(location = 0) in vec3 i_position;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

out vec3 v_viewPos;
out vec3 v_viewNormal;

void main() {
    // 对单位球，放大只需把位置向量整体乘一个系数
    vec3 scaledPos = i_position * 1.1;

    vec4 worldPos = u_model * vec4(scaledPos, 1.0);
    vec4 viewPos  = u_view * worldPos;
    gl_Position   = u_projection * viewPos;

    // 法线仍然使用“未放大”的方向（单位球表面方向）
    vec3 localNormal = normalize(i_position);
    v_viewNormal = normalize((u_view * u_model * vec4(localNormal, 0.0)).xyz);
    v_viewPos = viewPos.xyz;
}