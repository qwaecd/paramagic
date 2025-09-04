#version 330

layout(location = 0) in vec3 i_position;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;
uniform float u_time;

void main() {
    gl_Position = u_projection * u_view * u_model * vec4(i_position, 1.0);
}