#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

uniform sampler2D u_hdrSceneTexture; // 你后期处理完成的最终纹理
uniform float u_exposure = 1.0; // 曝光度控制

void main() {
    // 1. 从HDR纹理采样颜色
    vec3 hdrColor = texture(u_hdrSceneTexture, v_texCoords).rgb;

    // 2. 曝光控制
    vec3 mappedColor = hdrColor * u_exposure;

    // 3. 色调映射 (Reinhard Tonemapping)
    // 将无限的HDR颜色范围映射到[0, 1]的LDR范围
//    mappedColor = mappedColor / (mappedColor + vec3(1.0));
    mappedColor = vec3(1.0) - exp(-hdrColor * u_exposure);
    // 4. Gamma校正
    // 将线性空间的颜色转换到sRGB空间，以在显示器上正确显示
    float gamma = 2.2;
    FragColor.rgb = pow(mappedColor, vec3(1.0 / gamma));
    FragColor.a = 1.0;
}
