#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;
in float gameTime;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    // Add magical glow effect
    float distance_from_center = length(texCoord0 - vec2(0.5, 0.5)) * 2.0;

    // Pulsing effect based on game time
    float pulse = sin(gameTime * 0.05) * 0.3 + 0.7;

    // Create magical glow
    float glow = 1.0 - distance_from_center;
    glow = pow(glow, 2.0) * pulse;

    // Enhance the magical effect
    color.rgb += vec3(0.3, 0.6, 1.0) * glow * 0.5;
    color.a *= glow;

    // Add subtle animation
    float wave = sin(gameTime * 0.1 + distance_from_center * 10.0) * 0.1 + 0.9;
    color.a *= wave;

    // Apply fog
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}