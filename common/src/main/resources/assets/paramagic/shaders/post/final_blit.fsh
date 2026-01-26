#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

uniform sampler2D u_hdrSceneTexture;
uniform sampler2D u_gameSceneTexture;
uniform float u_exposure = 1.0; // 曝光度
uniform bool u_enableGammaCorrection;
vec3 reinhardToneMapping(vec3 color) {
    color *= u_exposure;
    return color / (color + vec3(1.0));
}
// 另一个流行的ACES电影级色调映射近似算子
vec3 acesFilm(vec3 x) {
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);
}

void main() {
    const float gamma = 2.2;
    vec4 hdrSample = texture(u_hdrSceneTexture, v_texCoords);
    vec4 gameSample = texture(u_gameSceneTexture, v_texCoords);
    vec3 hdrColor = hdrSample.rgb;
    float alpha = hdrSample.a;
    // 色调映射
    vec3 ldrColor = acesFilm(hdrColor * u_exposure);
//    vec3 ldrColor = reinhardToneMapping(hdrColor * u_exposure);
    if (u_enableGammaCorrection) {
        ldrColor = pow(ldrColor, vec3(1.0 / gamma));
    }
    FragColor.rgb = ldrColor * (1.0 - gameSample.rgb);
    FragColor.a = alpha;
}
