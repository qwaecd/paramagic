#version 330 core
layout(location = 0) in vec3 a_pos;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

out vec2 v_localXZ;

void main() {
    vec4 world = u_model * vec4(a_pos, 1.0);
    gl_Position = u_projection * u_view * world;
    v_localXZ = a_pos.xz; // 本地 XZ，用于片元极坐标计算
}