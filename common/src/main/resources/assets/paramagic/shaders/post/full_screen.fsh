#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

// 输入1: 原始场景纹理 (来自 mainFbo 的 attachment 0)
uniform sampler2D u_sceneTexture;

// 输入2: 经过模糊和升采样处理后的最终辉光纹理
uniform sampler2D u_bloomTexture;

// 可选：控制辉光的强度
uniform float u_bloomStrength = 1.0;

void main() {
    // 从两个纹理中采样颜色
    vec4 scene = texture(u_sceneTexture, v_texCoords);
    vec3 sceneColor = scene.rgb;
    float alpha = scene.a;
    vec3 bloomColor = texture(u_bloomTexture, v_texCoords).rgb;

    // 将辉光颜色加到场景颜色上
    // 这是最基础的加法混合
    vec3 finalColor = sceneColor + bloomColor * u_bloomStrength;

    FragColor = vec4(finalColor, alpha);
}
