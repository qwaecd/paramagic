#version 330

in vec2 v_uv;
in vec3 v_viewPos;
in vec3 v_viewNormal;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

uniform sampler2D u_flowTexture;
uniform sampler2D u_noiseTexture;
uniform vec3 u_color;
uniform float u_alpha;
uniform float u_threshold;
uniform float u_softness;
uniform float u_emissiveIntensity;
uniform vec2 u_uvScale;
uniform vec2 u_flowSpeed;
uniform vec2 u_noiseSpeed;
uniform float u_noiseScale;
uniform float u_noiseStrength;
uniform float u_time;

void main() {
    vec2 uv = v_uv * u_uvScale;
    float flow = texture(u_flowTexture, uv + u_flowSpeed * u_time).r;
    float noise = texture(u_noiseTexture, uv * u_noiseScale + u_noiseSpeed * u_time).r;

    float mask = smoothstep(u_threshold, u_threshold + max(u_softness, 0.0001), flow);
    float noiseMask = mix(1.0, noise, clamp(u_noiseStrength, 0.0, 1.0));

    vec3 viewNormal = normalize(v_viewNormal);
    vec3 viewDir = normalize(-v_viewPos);
    float rim = pow(1.0 - max(dot(viewNormal, viewDir), 0.0), 2.0);

    float alpha = mask * noiseMask * u_alpha;
    vec3 color = u_color * alpha * (1.0 + rim * 0.35);
    vec3 bloomColor = u_color * u_emissiveIntensity * alpha;

    o_color = vec4(color, alpha);
    o_bloomColor = vec4(bloomColor, 1.0);
}
