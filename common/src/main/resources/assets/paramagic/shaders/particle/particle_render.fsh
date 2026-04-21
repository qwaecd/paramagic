#version 430 core

in ParticleVaryings {
    vec4 color;
    float intensity;
    float size;
    float angle;
    vec3 centerView;
    vec3 normalView;
    flat uint facingMode;
} particleIn;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

void main() {
    vec3 growHDRColor = particleIn.color.rgb * particleIn.intensity;
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    const float threshold = 0.1;
    if (particleIn.intensity > threshold) {
        o_bloomColor = vec4(growHDRColor * particleIn.color.a, 1.0);
    }
    o_color = particleIn.color;
}
