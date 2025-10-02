#version 330 core

in vec4 v_color;
in float v_intensity;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

void main() {
    vec3 growHDRColor = v_color.rgb * v_intensity;
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    const float threshold = 0.1;
    if (v_intensity > threshold) {
        o_bloomColor = vec4(growHDRColor - threshold, 1.0);
    }
    o_color = v_color;
}