#version 330

layout(location = 0) in vec3 i_position;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

out vec3 v_viewPos;
out vec3 v_viewNormal;

void main() {
    // 世界 → 视图 → 裁剪坐标
    vec4 worldPos = u_model * vec4(i_position, 1.0);
    vec4 viewPos = u_view * worldPos;
    gl_Position = u_projection * viewPos;

    // 对单位球：局部坐标方向即法线方向（假设只做等比缩放）
    // 直接把法线转到视图空间，便于在片段里做光照/边缘高亮
    vec3 localNormal = normalize(i_position);
    v_viewNormal = normalize((u_view * u_model * vec4(localNormal, 0.0)).xyz);
    v_viewPos = viewPos.xyz;
}