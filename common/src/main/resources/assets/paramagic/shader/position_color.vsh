#version 330

layout(location = 0) in vec3 i_position;
layout(location = 1) in vec4 i_color;

uniform mat4 u_projection;
out vec4 vertex_color;

void main() {
    gl_Position = u_projection * vec4(i_position, 1.0);
    vertex_color = i_color;
}