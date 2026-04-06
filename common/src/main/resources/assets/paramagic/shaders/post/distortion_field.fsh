#version 330 core

layout(location = 0) out vec4 FragColor;

uniform vec2 u_centerUv;
uniform vec2 u_viewportSize;
uniform float u_distortionStrength;
uniform float u_innerRadius;
uniform float u_outerRadius;
uniform float u_maxOffset;

void main() {
    vec2 uv = gl_FragCoord.xy / u_viewportSize;
    vec2 toCenter = u_centerUv - uv;
    float r = length(toCenter);
    float safeR = max(r, u_innerRadius);
    float edgeFade = 1.0 - smoothstep(u_outerRadius * 0.7, u_outerRadius, r);
    vec2 direction = (r > 1e-5) ? (toCenter / r) : vec2(0.0);
    float offsetMagnitude = min((u_distortionStrength / safeR) * edgeFade, u_maxOffset);
    vec2 distortion = direction * offsetMagnitude;
    FragColor = vec4(distortion, offsetMagnitude, 1.0);
}
