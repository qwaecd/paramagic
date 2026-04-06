#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

uniform sampler2D u_hdrSceneTexture;
uniform sampler2D u_gameSceneTexture;
uniform float u_exposure;
uniform bool u_enableGammaCorrection;

vec3 acesFilm(vec3 x) {
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

void main() {
    const float gamma = 2.2;

    vec3 hdrColor = texture(u_hdrSceneTexture, v_texCoords).rgb;
    vec3 gameColor = texture(u_gameSceneTexture, v_texCoords).rgb;
    vec3 ldrColor = acesFilm(hdrColor * u_exposure);
    if (u_enableGammaCorrection) {
        ldrColor = pow(ldrColor, vec3(1.0 / gamma));
    }

    vec3 finalColor = gameColor + ldrColor * (1.0 - gameColor);
    FragColor = vec4(finalColor, 1.0);
}
