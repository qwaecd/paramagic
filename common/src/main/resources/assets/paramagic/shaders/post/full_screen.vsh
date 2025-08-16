#version 330 core

layout(location = 0) in vec3 i_position;
layout(location = 2) in vec2 i_texCoords;

out vec2 v_texCoords;
void main() {
    gl_Position = vec4(i_position.xy, 0.0, 1.0);
    v_texCoords = i_texCoords;
}