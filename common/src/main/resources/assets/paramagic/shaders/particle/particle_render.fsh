#version 330 core

in vec4 v_color;
in vec2 v_texCoord; // ignore it, because not impl
layout (location = 5) in float in_intensity;


layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

void main() {
    vec3 growHDRColor = v_color.rgb * in_intensity;
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    const float threshold = 0.1;
    if (in_intensity > threshold) {
        o_bloomColor = vec4(growHDRColor - threshold, 1.0);
    }
    o_color = v_color;
}