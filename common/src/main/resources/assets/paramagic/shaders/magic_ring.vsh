#version 330

layout(location = 0) in vec3 i_position;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;
uniform float u_time;

out vec2 v_localXZ;

void main() {
    // 轻微几何“呼吸”脉动（可注释掉）
    float r = length(i_position.xz);
    float pulse = 1.0 + 0.03 * sin(u_time * 2.0 + r * 6.0);
    vec3 pos = vec3(i_position.x * pulse, i_position.y, i_position.z * pulse);

    gl_Position = u_projection * u_view * u_model * vec4(pos, 1.0);
    // 传给片段着色器的“平面局部坐标”（用于极坐标计算）
    v_localXZ = i_position.xz;
}
