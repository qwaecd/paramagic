#version 430 core

in ParticleVaryings {
    vec4 color;
    float intensity;
    float size;
    float angle;
    vec3 centerView;
    vec3 normalView;
    flat uint particleSeed;
    flat uint shapeFlags;
    flat uint facingMode;
    vec2 uv;
} particleIn;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

uniform int u_textured;
uniform sampler2D u_particleTexture;
uniform float u_alphaCutoff;

void main() {
    vec4 color = particleIn.color;
    if (u_textured != 0) {
        vec4 texel = texture(u_particleTexture, particleIn.uv);
        if (texel.a * particleIn.color.a < u_alphaCutoff) {
            discard;
        }
        color = vec4(texel.rgb * particleIn.color.rgb, 1.0);
    }

    vec3 growHDRColor = color.rgb * particleIn.intensity;
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    const float threshold = 0.1;
    if (particleIn.intensity > threshold) {
        o_bloomColor = vec4(growHDRColor * color.a, 1.0);
    }
    o_color = color;
}
