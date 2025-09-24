#version 430

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

void main() {
    o_color = vec4(1.0, 0.7, 0.2, 1.0);
    o_bloomColor = vec4(0.0, 0.7, 0.2, 0.4);
}