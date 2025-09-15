#version 330

layout(location = 0) in vec3 i_position;
layout(location = 1) in vec4 i_color;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;
uniform float u_time;
uniform bool u_hasColorAnimation;
uniform vec4 u_animationColor;
out vec4 vertex_color;

void main() {
    gl_Position = u_projection * u_view * u_model * vec4(i_position, 1.0);
    if (u_hasColorAnimation) {
        vertex_color = u_animationColor;
    } else {
        vertex_color = i_color;
    }
}