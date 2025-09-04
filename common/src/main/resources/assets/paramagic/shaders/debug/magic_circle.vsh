#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aUV;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

out vec2 vUV;

void main() {
    vUV = aUV;
    gl_Position = u_projection * u_view * u_model * vec4(aPos, 1.0);
}