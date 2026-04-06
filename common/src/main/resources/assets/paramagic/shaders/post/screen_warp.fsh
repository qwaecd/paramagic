#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

uniform sampler2D u_sceneTexture;
uniform sampler2D u_distortionFieldTexture;

void main() {
    vec2 offset = texture(u_distortionFieldTexture, v_texCoords).xy;
    vec2 warpedUv = clamp(v_texCoords + offset, vec2(0.001), vec2(0.999));
    FragColor = texture(u_sceneTexture, warpedUv);
}
